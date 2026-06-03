package org.codewithzea.doccasetracker.service;

import org.codewithzea.doccasetracker.dto.request.*;
import org.codewithzea.doccasetracker.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String refreshToken);
    void forgotPassword(ForgotPasswordRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
