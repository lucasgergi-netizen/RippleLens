package com.RippleLens.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("${ripplelens.security.password-reset.delivery-enabled:false}"
        + " and '${ripplelens.security.password-reset.provider:none}' == 'smtp'")
public class SmtpPasswordResetDeliveryService implements PasswordResetDeliveryService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpPasswordResetDeliveryService(
            JavaMailSender mailSender,
            @Value("${ripplelens.security.password-reset.from-address:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendResetLink(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Reset your RippleLens password");
        message.setText("A password reset was requested for your RippleLens account.\n\n"
                + resetLink
                + "\n\nThis link expires in 15 minutes and works only once. "
                + "If you did not request it, you can ignore this email.");
        mailSender.send(message);
    }
}
