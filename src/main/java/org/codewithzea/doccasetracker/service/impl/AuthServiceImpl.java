package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.*;
import org.codewithzea.doccasetracker.dto.response.AuthResponse;
import org.codewithzea.doccasetracker.entity.*;
import org.codewithzea.doccasetracker.exception.*;
import org.codewithzea.doccasetracker.mapper.UserMapper;
import org.codewithzea.doccasetracker.repository.RoleRepository;
import org.codewithzea.doccasetracker.repository.RefreshTokenRepository;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.codewithzea.doccasetracker.security.JwtUtils;
import org.codewithzea.doccasetracker.service.AuthService;
import org.codewithzea.doccasetracker.service.EmailService;
import org.codewithzea.doccasetracker.service.OtpService;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.codewithzea.doccasetracker.util.UserQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.codewithzea.doccasetracker.util.AuditActions.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final OtpService otpService;
    private final AuditLogService auditLogService;

    private final UserQueryService userQueryService;

    @Value("${app.jwt.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;


    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#request.email")
    public AuthResponse register(RegisterRequest request) {

        log.debug("REGISTER: Start registration for email={}", request.getEmail());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("REGISTER: Password mismatch for email={}", request.getEmail());
            throw new InvalidCredentialsException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("REGISTER: Email already exists email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("REGISTER: Phone already exists phone={}", request.getPhoneNumber());
            throw new PhoneNumberAlreadyExistsException("Phone number is already registered");
        }

        log.debug("REGISTER: Validations passed for email={}", request.getEmail());

        RoleType roleType = switch (request.getRole()) {
            case WORKER -> RoleType.ROLE_WORKER;
            case MANAGER -> RoleType.ROLE_MANAGER;
            case ADMIN ->  RoleType.ROLE_ADMIN;
        };

        log.debug("REGISTER: Role resolved roleType={}", roleType);

        Role selectedRole = roleRepository.findByName(roleType)
                .orElseThrow(() -> {
                    log.error("REGISTER: Role not found roleType={}", roleType);
                    return new RuntimeException("Role not found: " + request.getRole());
                });

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(selectedRole)
                .approvalStatus(ApprovalStatus.PENDING)
                .enabled(false)
                .accountNonLocked(true)
                .build();

        User savedUser = userRepository.save(user);

        log.info("REGISTER: User created id={} email={}", savedUser.getId(), savedUser.getEmail());

        String accessToken = jwtUtils.generateToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser);

        log.debug("REGISTER: Tokens generated for userId={}", savedUser.getId());

        audit(USER_REGISTERED,user,"New User registered", "USER");


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(savedUser))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        log.debug("LOGIN: Attempt start email={}", request.getEmail());


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        log.debug("LOGIN: Authentication successful email={}", request.getEmail());

        User user = getUser(authentication);

        log.debug("LOGIN: User loaded id={} status={}", user.getId(), user.getApprovalStatus());

        String accessToken = jwtUtils.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        log.info("LOGIN: Successful login userId={} email={}", user.getId(), user.getEmail());

        audit(USER_LOGIN,user,"User logged into the system", "AUTH");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toDto(user))
                .build();
    }

    private static User getUser(Authentication authentication) {

        log.debug("AUTH: Extracting user from authentication");

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            log.error("AUTH: Principal is null after authentication");
            throw new InvalidCredentialsException("Authentication failed");
        }

        log.debug("AUTH: User found id={} status={}", user.getId(), user.getApprovalStatus());

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {

            log.warn("AUTH: Login blocked userId={} status={}",
                    user.getId(), user.getApprovalStatus());

            if (user.getApprovalStatus() == ApprovalStatus.REJECTED) {
                throw new AccountNotApprovedException("Your account has been rejected");
            }

            throw new AccountNotApprovedException("Account pending admin approval");
        }

        return user;
    }

    @Override
    @Transactional
    public void logout(String token) {

        log.debug("LOGOUT: Attempt started");

        if (token == null || token.isEmpty()) {
            log.warn("LOGOUT: Empty token received");
            return;
        }

        refreshTokenRepository.findByToken(token)
                .ifPresentOrElse(rt -> {

                    log.debug("LOGOUT: Refresh token found userId={}", rt.getUser().getId());

                    User user = rt.getUser();

                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);

                    if (user != null) {
                        audit(USER_LOGOUT, user, "User logged out", "AUTH");
                    }

                    log.info("LOGOUT: Success userId={} email={}", user.getId(), user.getEmail());

                }, () -> log.warn("LOGOUT: Token not found in DB"));
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        log.debug("FORGOT_PASSWORD: Request received email={}", request.getEmail());

        User user = userQueryService.getUserByEmail(request.getEmail());

        log.info("FORGOT_PASSWORD: User found id={} email={}", user.getId(), user.getEmail());

        OtpVerification otp = otpService.generateOtp(user.getEmail());

        log.debug("FORGOT_PASSWORD: OTP generated email={} otpId={}", user.getEmail(), otp.getId());

        emailService.sendOtpEmail(user.getEmail(), otp.getOtp());

        log.info("FORGOT_PASSWORD: OTP sent successfully email={}", user.getEmail());

        audit(PASSWORD_RESET_OTP_REQUESTED,user,"User requested password reset OTP", "AUTH");

        log.debug("FORGOT_PASSWORD: Process completed email={}", user.getEmail());
    }

    @Override
    @Transactional
    public boolean verifyOtp(VerifyOtpRequest request) {

        boolean verified =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        if (verified) {

            User user = userQueryService.getUserByEmail(request.getEmail());

            audit(PASSWORD_RESET_OTP_VERIFIED,user,"User verified password reset OTP", "AUTH");

        }

        return verified;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#request.email")
    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        User user = userQueryService.getUserByEmail(request.getEmail());

        if (!otpService.isOtpVerifiedAndValid(user.getEmail(), request.getOtp())) {
            throw new InvalidOtpException("OTP code is invalid or has not been verified yet");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpService.markOtpAsUsed(user.getEmail(), request.getOtp());

        // 🔥 SAFE REFRESH TOKEN INVALIDATION
        refreshTokenRepository.findAll()
                .stream()
                .filter(rt -> rt.getUser().getId().equals(user.getId()))
                .forEach(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });

        emailService.sendPasswordResetConfirmationEmail(user.getEmail());

        audit(PASSWORD_RESET, user, "Password reset successfully", "USER");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        log.debug("REFRESH: Start rotation");

        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> {
                    log.warn("REFRESH: Invalid refresh token");
                    return new InvalidCredentialsException("Invalid refresh token");
                });

        if (token.isRevoked()) {
            log.warn("REFRESH: Token revoked userId={}", token.getUser().getId());
            throw new InvalidCredentialsException("Refresh token was revoked");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            log.warn("REFRESH: Token expired userId={}", token.getUser().getId());
            throw new RefreshTokenExpiredException("Refresh token has expired. Please log in again.");
        }

        User user = token.getUser();

        log.debug("REFRESH: Token valid userId={}", user.getId());

        // 🔥 SAFE: revoke instead of delete (prevents race conditions)
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        // generate new tokens
        String accessToken = jwtUtils.generateToken(user);
        RefreshToken newRefreshToken = createRefreshToken(user);

        log.info("REFRESH: Token rotated userId={}", user.getId());

        audit(REFRESH_TOKEN_ROTATED, user, "Refresh Token rotated", "AUTH");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(userMapper.toDto(user))
                .build();
    }

    /**
     * Creates a new refresh token for the given user.
     *
     * @param user The user for whom to create the refresh token (must not be null)
     * @return A new RefreshToken entity
     * @throws IllegalArgumentException if user is null
     */
    private RefreshToken createRefreshToken(User user) {

        log.debug("REFRESH: Creating token for userId={}", user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        log.debug("REFRESH: Token created id={} userId={}", saved.getId(), user.getId());

        return saved;
    }


    private void audit(String action, User user, String details, String entityType) {
        auditLogService.log(
                action,
                entityType,
                user.getId(),
                details,
                user.getEmail(),
                user.getId(),
                user.getRole().getName().name()
        );
    }

}