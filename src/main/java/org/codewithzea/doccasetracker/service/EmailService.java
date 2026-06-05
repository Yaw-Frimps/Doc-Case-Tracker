package org.codewithzea.doccasetracker.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetConfirmationEmail(String toEmail);
    void sendAccountApprovedEmail(String toEmail, String fullName);
    void sendAccountRejectedEmail(String toEmail, String fullName);
}
