package org.codewithzea.doccasetracker.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetConfirmationEmail(String toEmail);
}
