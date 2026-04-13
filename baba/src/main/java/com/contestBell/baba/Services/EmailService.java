package com.contestBell.baba.Services;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${app.base-url}")
    private String baseurl;

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    private void sendEmail(String toEmail, String toName, String subject, String textContent) {
        try {
            Map<String, Object> body = Map.of(
                    "sender", Map.of("email", senderEmail, "name", senderName),
                    "to", new Object[]{Map.of("email", toEmail, "name", toName)},
                    "subject", subject,
                    "textContent", textContent
            );

            webClient.post()
                    .uri("/smtp/email")
                    .header("api-key", brevoApiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendVerificationMail(String sendTo, String token) {
        String link = baseurl + "/auth/verify?token=" + token;
        String text =
                "Hello,\n\n" +
                        "Thanks for joining us! We're glad you're here 🎉\n\n" +
                        "Please verify your email address to activate your account:\n\n" +
                        link +
                        "\n\nThis helps us keep your account secure.\n\n" +
                        "Cheers,\n" +
                        "Sahil from ContestBell\n\n" +
                        "\"Hit every contest Harder💪\"";

        sendEmail(sendTo, "User", "To verify Your CONTEST-BELL account", text);
    }

    public void sendPasswordResetMail(String sendTo, String token) {
        String link = baseurl + "/auth/reset-password?token=" + token;
        String text =
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset it:\n\n" +
                        link +
                        "\n\nThis link expires in 15 minutes.\n\n" +
                        "If you didn't request this, ignore this email.\n\n" +
                        "Cheers,\n" +
                        "Sahil from ContestBell";

        sendEmail(sendTo, "User", "Reset your ContestBell password", text);
    }

    public void sendContestNotification(User user, Contest contest, String notificationType) {
        try {
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

            sendEmail(user.getEmail(), user.getName(), subject, body);

        } catch (Exception e) {
            log.error("Failed to send notification", e);
            throw e;
        }
    }
}