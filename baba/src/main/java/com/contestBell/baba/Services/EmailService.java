package com.contestBell.baba.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseurl;

    public void sendVerificationMail(String sendTo, String token) {
        try {
            String link = baseurl + "/auth/verify?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(sendTo);
            message.setSubject("To verify Your CONTEST-BELL account");
            message.setText(
                    "Hello,\n\n" +
                            "Thanks for joining us! We're glad you're here 🎉\n\n" +
                            "Please verify your email address to activate your account:\n\n" +
                            link +
                            "\n\nThis helps us keep your account secure.\n\n" +
                            "Cheers,\n" +
                            "Sahil from ContestBell\n\n" +
                            "\"Hit every contest Harder💪\""
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("failed to send mail", e);
        }
    }
}