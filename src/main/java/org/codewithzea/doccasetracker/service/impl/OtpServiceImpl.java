package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.entity.OtpVerification;
import org.codewithzea.doccasetracker.exception.InvalidOtpException;
import org.codewithzea.doccasetracker.exception.OtpExpiredException;
import org.codewithzea.doccasetracker.repository.OtpVerificationRepository;
import org.codewithzea.doccasetracker.service.OtpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final SecureRandom secureRandom = new SecureRandom();


    @Override
    @Transactional
    public OtpVerification generateOtp(String email) {
        int number = secureRandom.nextInt(900000) + 100000;
        String otp = String.valueOf(number);

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .verified(false)
                .used(false)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .build();

        return otpRepository.save(otpVerification);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        OtpVerification otpVerification = otpRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new InvalidOtpException("No OTP found for this email"));

        if (!otpVerification.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid OTP code");
        }

        if (otpVerification.isUsed()) {
            throw new InvalidOtpException("OTP has already been used");
        }

        if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired");
        }

        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOtpVerifiedAndValid(String email, String otp) {
        return otpRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .map(v -> v.getOtp().equals(otp) && v.isVerified() && !v.isUsed() && v.getExpiryTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void markOtpAsUsed(String email, String otp) {
        otpRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .ifPresent(v -> {
                    if (v.getOtp().equals(otp)) {
                        v.setUsed(true);
                        otpRepository.save(v);
                    }
                });
    }
}
