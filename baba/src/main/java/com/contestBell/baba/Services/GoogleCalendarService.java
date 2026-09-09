package com.contestBell.baba.Services;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Entity.User;
import com.contestBell.baba.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoogleCalendarService {
    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Autowired
    private UserRepository userRepository;

    private static final String SCOPES =
            "https://www.googleapis.com/auth/calendar.events" +
                    "+https://www.googleapis.com/auth/userinfo.email" +
                    "+https://www.googleapis.com/auth/userinfo.profile";

    private static final String CALENDAR_API =
            "https://www.googleapis.com/calendar/v3/calendars/primary/events";


    public String generateAuthUrl(String userId) {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + SCOPES
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + userId;
    }

    public void handleCallBack(String code, String userId) throws Exception {
        //exchange auth code with tokens
        String tokenUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl, request, Map.class);

        Map<String, Object> tokens = response.getBody();

        String accessToken = (String) tokens.get("access_token");
        String refreshToken = (String) tokens.get("refresh_token");

        //get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setGoogleAccessToken(accessToken);
        user.setGoogleRefreshToken(refreshToken);
        user.setCalendarConnected(true);
        userRepository.save(user);

        log.info("Google calender Connected for user {}", user.getEmail());
    }

    //service to get new access token by refresh token
    public String refreshAccessToken(User user) throws Exception {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", user.getGoogleRefreshToken());
        params.add("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map<String, Object> tokens = response.getBody();
        String newAccessToken = (String) tokens.get("access_token");

        user.setGoogleAccessToken(newAccessToken);
        userRepository.save(user);

        log.info("Access token refreshed of user {}", user.getEmail());

        return newAccessToken;
    }

    //service to create event in calendar of user
    public void createCalenderEvent(User user, Contest contest) {
        log.info("Attempting calendar event for user {} connected={}",
                user.getEmail(), user.isCalendarConnected());
        try {
            if (!user.isCalendarConnected()
                    || user.getGoogleAccessToken() == null) {
                return;
            }

            //convert to user zonetime
            ZoneId userZone = ZoneId.of(user.getTimezone() != null
                    ? user.getTimezone() : "Asia/Kolkata");

            ZonedDateTime start = contest.getStartTimeUtc()
                    .atZone(ZoneOffset.UTC)
                    .withZoneSameInstant(userZone);

            ZonedDateTime end = start.plusSeconds(contest.getDurationSeconds());

            //build event
            Map<String, Object> event = new HashMap<>();
            event.put("summary", "🦖" + contest.getName());
            event.put("description",
                    "Platform: " + contest.getPlatform() +
                            "\nDivision: " + contest.getDivision() +
                            "\nContest Link: " + contest.getContestUrl() +
                            "\n\nGood luck! 💪 — ContestBell");

            Map<String, String> startTime = new HashMap<>();
            startTime.put("dateTime", start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            startTime.put("timeZone", userZone.getId());

            Map<String, String> endTime = new HashMap<>();
            endTime.put("dateTime", end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            endTime.put("timeZone", userZone.getId());

            event.put("start", startTime);
            event.put("end", endTime);

            //reminders
            Map<String, Object> reminders = new HashMap<>();
            reminders.put("useDefault", false);
            reminders.put("overrides", List.of(
                    Map.of("method", "popup", "minutes", 120), // 2 hours before
                    Map.of("method", "popup", "minutes", 30)   // 30 minutes before
            ));
            event.put("reminders", reminders);
            tryCreateEvent(user, event);
        } catch (Exception e) {
            log.error("Failed to create calender event for user {}", user.getEmail(), e);
        }
    }

    public void tryCreateEvent(User user, Map<String, Object> event) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getGoogleAccessToken());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event, headers);

        try {
            restTemplate.postForEntity(CALENDAR_API, request, Map.class);
            log.info("Calender event created for user {}", user.getEmail());
        } catch (HttpClientErrorException.Unauthorized e) {
            //token expired try getting new token and retry
            log.info("Token expired for user {} - refreshing", user.getEmail());
            try {
                String newToken = refreshAccessToken(user);
                headers.setBearerAuth(newToken);
                HttpEntity<Map<String, Object>> retryRequest =
                        new HttpEntity<>(event, headers);
                restTemplate.postForEntity(CALENDAR_API, retryRequest, Map.class);
                log.info("Calender event created after token refresh for user {}", user.getEmail());

            } catch (Exception refreshException) {
                //if refresh token is invalid mark as disconnected
                log.error("Refresh token invalid for user {} - marking as disconnected",
                        user.getEmail());
                user.setCalendarConnected(false);
                user.setGoogleAccessToken(null);
                user.setGoogleRefreshToken(null);
                userRepository.save(user);
            }
        }
    }
}
