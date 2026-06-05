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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {

        Context context = new Context();
        context.setVariable("otp", otp);

        String htmlContent =
                templateEngine.process("email/otp-email", context);

        sendHtmlEmail(
                toEmail,
                "Password Reset OTP",
                htmlContent
        );
    }

    @Override
    @Async
    public void sendPasswordResetConfirmationEmail(String toEmail) {

        Context context = new Context();

        String htmlContent =
                templateEngine.process(
                        "email/password-reset-success",
                        context
                );

        sendHtmlEmail(
                toEmail,
                "Password Changed Successfully",
                htmlContent
        );
    }

    @Override
    @Async
    public void sendAccountApprovedEmail(
            String toEmail,
            String fullName
    ) {

        Context context = new Context();
        context.setVariable("fullName", fullName);

        String htmlContent =
                templateEngine.process(
                        "email/account-approved",
                        context
                );

        sendHtmlEmail(
                toEmail,
                "Account Approved",
                htmlContent
        );
    }

    @Override
    @Async
    public void sendAccountRejectedEmail(
            String toEmail,
            String fullName
    ) {

        Context context = new Context();
        context.setVariable("fullName", fullName);

        String htmlContent =
                templateEngine.process(
                        "email/account-rejected",
                        context
                );

        sendHtmlEmail(
                toEmail,
                "Account Approval Update",
                htmlContent
        );
    }

    private void sendHtmlEmail(
            String toEmail,
            String subject,
            String htmlContent
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                            StandardCharsets.UTF_8.name()
                    );

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info(
                    "Email sent successfully to {} with subject {}",
                    toEmail,
                    subject
            );

        } catch (MessagingException e) {

            logger.error(
                    "Failed to send email to {}",
                    toEmail,
                    e
            );
        }
    }

}
