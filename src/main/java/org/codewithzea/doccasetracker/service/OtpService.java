package org.codewithzea.doccasetracker.service;

import org.codewithzea.doccasetracker.entity.OtpVerification;

public interface OtpService {
    OtpVerification generateOtp(String email);
    boolean verifyOtp(String email, String otp);
    boolean isOtpVerifiedAndValid(String email, String otp);
    void markOtpAsUsed(String email, String otp);
}
