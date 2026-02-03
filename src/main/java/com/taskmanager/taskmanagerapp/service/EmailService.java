package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.repository.EmailVerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // ── Verification Email ──────────────────────────────────
    // verificationUrl = the full clickable link we embed in HTML
    public void sendVerificationEmail(String recipientEmail, String username, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = multipart (needed for HTML emails)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("✅ Verify Your Email Address");

            String htmlBody = buildVerificationEmailHtml(username, verificationUrl);
            // true = content is HTML, not plain text
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", recipientEmail, e);
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }
    }

    // ── Resend Verification Email ───────────────────────────
    // Identical structure, just different log message for clarity
    public void sendResendVerificationEmail(String recipientEmail, String username, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("📧 Resend: Verify Your Email Address");

            String htmlBody = buildResendVerificationEmailHtml(username, verificationUrl);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Resend verification email sent to: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("Failed to resend verification email to: {}", recipientEmail, e);
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }
    }

    // ── HTML Template: First-time Verification ─────────────
    private String buildVerificationEmailHtml(String username, String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #667eea, #764ba2); padding: 40px 30px; text-align: center; }
                        .header h1 { color: #ffffff; margin: 0; font-size: 26px; }
                        .header p { color: #e8e0ff; margin: 8px 0 0; font-size: 14px; }
                        .body { padding: 35px 30px; }
                        .greeting { font-size: 18px; color: #333; margin-bottom: 12px; }
                        .body p { color: #666; font-size: 15px; line-height: 1.6; margin: 0 0 18px; }
                        .button-container { text-align: center; margin: 30px 0; }
                        .verify-btn { display: inline-block; background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; padding: 14px 36px; border-radius: 8px; text-decoration: none; font-size: 16px; font-weight: bold; }
                        .verify-btn:hover { opacity: 0.9; }
                        .expiry-note { background: #fff3cd; border-left: 4px solid #ffc107; padding: 12px 16px; border-radius: 0 6px 6px 0; margin: 20px 0; }
                        .expiry-note p { color: #856404; font-size: 13px; margin: 0; }
                        .link-fallback { margin-top: 20px; }
                        .link-fallback p { font-size: 13px; color: #999; margin-bottom: 4px; }
                        .link-fallback a { color: #667eea; font-size: 12px; word-break: break-all; }
                        .footer { background: #f4f6f9; padding: 20px 30px; text-align: center; }
                        .footer p { color: #999; font-size: 12px; margin: 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📧 Email Verification</h1>
                            <p>JWT Auth API</p>
                        </div>
                        <div class="body">
                            <p class="greeting">Hello, <strong>%s</strong>!</p>
                            <p>Thank you for registering. To complete your account setup, please verify your email address by clicking the button below.</p>
                            <div class="button-container">
                                <a href="%s" class="verify-btn">Verify My Email</a>
                            </div>
                            <div class="expiry-note">
                                <p>⏰ This link will expire in <strong>24 hours</strong>. If it expires, you can request a new one.</p>
                            </div>
                            <div class="link-fallback">
                                <p>If the button doesn't work, copy and paste this link:</p>
                                <a href="%s">%s</a>
                            </div>
                        </div>
                        <div class="footer">
                            <p>If you did not create an account, please ignore this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(username, verificationUrl, verificationUrl, verificationUrl);
    }

    // ── HTML Template: Resend Verification ─────────────────
    private String buildResendVerificationEmailHtml(String username, String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #43a047, #66bb6a); padding: 40px 30px; text-align: center; }
                        .header h1 { color: #ffffff; margin: 0; font-size: 26px; }
                        .header p { color: #e8f5e9; margin: 8px 0 0; font-size: 14px; }
                        .body { padding: 35px 30px; }
                        .greeting { font-size: 18px; color: #333; margin-bottom: 12px; }
                        .body p { color: #666; font-size: 15px; line-height: 1.6; margin: 0 0 18px; }
                        .button-container { text-align: center; margin: 30px 0; }
                        .verify-btn { display: inline-block; background: linear-gradient(135deg, #43a047, #66bb6a); color: #fff; padding: 14px 36px; border-radius: 8px; text-decoration: none; font-size: 16px; font-weight: bold; }
                        .expiry-note { background: #fff3cd; border-left: 4px solid #ffc107; padding: 12px 16px; border-radius: 0 6px 6px 0; margin: 20px 0; }
                        .expiry-note p { color: #856404; font-size: 13px; margin: 0; }
                        .link-fallback { margin-top: 20px; }
                        .link-fallback p { font-size: 13px; color: #999; margin-bottom: 4px; }
                        .link-fallback a { color: #43a047; font-size: 12px; word-break: break-all; }
                        .footer { background: #f4f6f9; padding: 20px 30px; text-align: center; }
                        .footer p { color: #999; font-size: 12px; margin: 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📧 Resend Verification</h1>
                            <p>JWT Auth API</p>
                        </div>
                        <div class="body">
                            <p class="greeting">Hello, <strong>%s</strong>!</p>
                            <p>You requested a new verification link. Click the button below to verify your email address.</p>
                            <div class="button-container">
                                <a href="%s" class="verify-btn">Verify My Email</a>
                            </div>
                            <div class="expiry-note">
                                <p>⏰ This new link will expire in <strong>24 hours</strong>. Previous links are now invalid.</p>
                            </div>
                            <div class="link-fallback">
                                <p>If the button doesn't work, copy and paste this link:</p>
                                <a href="%s">%s</a>
                            </div>
                        </div>
                        <div class="footer">
                            <p>If you did not request this email, please ignore it.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(username, verificationUrl, verificationUrl, verificationUrl);
    }

}
