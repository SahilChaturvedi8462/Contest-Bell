package com.contestBell.baba.Services;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public void sendPasswordResetMail(String sendTo, String token) {
        try {
            String link = baseurl + "/auth/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(sendTo);
            message.setSubject("Reset your ContestBell password");
            message.setText(
                    "Hello,\n\n" +
                            "We received a request to reset your password.\n\n" +
                            "Click the link below to reset it:\n\n" +
                            link +
                            "\n\nThis link expires in 15 minutes.\n\n" +
                            "If you didn't request this, ignore this email.\n\n" +
                            "Cheers,\n" +
                            "Sahil from ContestBell"
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("failed to send password reset mail", e);
        }
    }

    public void sendContestNotification(User user
            , Contest contest, String notificationType) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());

            //convert UTC to user local date and time
            ZoneId userZone = (user.getTimezone() != null)
                    ? ZoneId.of(user.getTimezone())
                    : ZoneId.of("Asia/Kolkata");
            ZonedDateTime userLocalTime = contest.getStartTimeUtc()
                    .atZone(ZoneOffset.UTC)
                    .withZoneSameInstant(userZone);

            String formattedTime = userLocalTime.format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a z"));

            String subject;
            String body;

            if ("DAY_BEFORE".equals(notificationType)) {
                subject = "🔔 Tomorrow: " + contest.getName();
                body = "Hey " + user.getName() + "!\n\n" +
                        "Reminder — this contest is tomorrow!\n\n" +
                        "📌 " + contest.getName() + "\n" +
                        "🏆 Platform: " + contest.getPlatform() + "\n" +
                        "📊 Division: " + contest.getDivision() + "\n" +
                        "⏰ Starts at: " + formattedTime + "\n" +
                        "🔗 " + contest.getContestUrl() + "\n\n" +
                        "Good luck! 💪\n" +
                        "ContestBell";

            } else if ("HOUR_BEFORE".equals(notificationType)) {
                subject = "⚡ Starting in 1 hour: " + contest.getName();
                body = "Hey " + user.getName() + "!\n\n" +
                        "Contest starts in 1 hour — time to warm up!\n\n" +
                        "📌 " + contest.getName() + "\n" +
                        "🏆 Platform: " + contest.getPlatform() + "\n" +
                        "📊 Division: " + contest.getDivision() + "\n" +
                        "⏰ Starts at: " + formattedTime + "\n" +
                        "🔗 " + contest.getContestUrl() + "\n\n" +
                        "You got this! 🔥\n" +
                        "ContestBell";

            } else {
                subject = "🆕 New Contest: " + contest.getName();
                body = "Hey " + user.getName() + "!\n\n" +
                        "A new contest has been added!\n\n" +
                        "📌 " + contest.getName() + "\n" +
                        "🏆 Platform: " + contest.getPlatform() + "\n" +
                        "📊 Division: " + contest.getDivision() + "\n" +
                        "⏰ Starts at: " + formattedTime + "\n" +
                        "🔗 " + contest.getContestUrl() + "\n\n" +
                        "ContestBell";
            }

            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("failed to send notification", e);
            throw e;
        }
    }
}