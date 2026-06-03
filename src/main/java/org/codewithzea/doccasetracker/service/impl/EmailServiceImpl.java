package org.codewithzea.doccasetracker.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String htmlContent = buildOtpEmailTemplate(otp);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("OTP email successfully sent to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", toEmail, e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetConfirmationEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String htmlContent = buildConfirmationEmailTemplate();

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Changed Successfully");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Password reset confirmation email successfully sent to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset confirmation email to {}", toEmail, e);
        }
    }

    private String buildOtpEmailTemplate(String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; color: #333; }" +
                "    .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); padding: 40px; border-top: 5px solid #0056b3; }" +
                "    .header { text-align: center; margin-bottom: 30px; }" +
                "    .header h2 { color: #0056b3; margin: 0; font-size: 26px; font-weight: 700; }" +
                "    .content { font-size: 16px; line-height: 1.6; color: #555555; }" +
                "    .otp-card { background: linear-gradient(135deg, #f0f7ff 0%, #e2f0ff 100%); border-radius: 8px; padding: 20px; text-align: center; margin: 25px 0; border: 1px solid #cce3ff; }" +
                "    .otp-code { font-size: 36px; font-weight: bold; letter-spacing: 5px; color: #0056b3; margin: 0; }" +
                "    .footer { font-size: 13px; color: #888888; text-align: center; margin-top: 35px; border-top: 1px solid #eeeeee; padding-top: 20px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h2>DocCaseTracker</h2>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Hello,</p>" +
                "      <p>We received a request to reset your password. Use the following One-Time Password (OTP) to proceed with resetting your password:</p>" +
                "      <div class='otp-card'>" +
                "        <h1 class='otp-code'>" + otp + "</h1>" +
                "      </div>" +
                "      <p><strong>This OTP is valid for 10 minutes.</strong> Please do not share this code with anyone for security purposes.</p>" +
                "      <p>If you did not request a password reset, you can safely ignore this email.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>This is an automated security system notification. Please do not reply to this email.</p>" +
                "      <p>&copy; 2026 DocCaseTracker. All rights reserved.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    private String buildConfirmationEmailTemplate() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; color: #333; }" +
                "    .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); padding: 40px; border-top: 5px solid #28a745; }" +
                "    .header { text-align: center; margin-bottom: 30px; }" +
                "    .header h2 { color: #28a745; margin: 0; font-size: 26px; font-weight: 700; }" +
                "    .content { font-size: 16px; line-height: 1.6; color: #555555; }" +
                "    .success-icon { text-align: center; font-size: 48px; color: #28a745; margin: 20px 0; }" +
                "    .footer { font-size: 13px; color: #888888; text-align: center; margin-top: 35px; border-top: 1px solid #eeeeee; padding-top: 20px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h2>DocCaseTracker</h2>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Hello,</p>" +
                "      <div class='success-icon'>&check;</div>" +
                "      <p>This email is to confirm that the password for your DocCaseTracker account has been <strong>changed successfully</strong>.</p>" +
                "      <p>If you made this change, no further action is required.</p>" +
                "      <p><strong>If you did not make this change</strong>, please contact our support team immediately and secure your account.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>This is an automated security system notification. Please do not reply to this email.</p>" +
                "      <p>&copy; 2026 DocCaseTracker. All rights reserved.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
