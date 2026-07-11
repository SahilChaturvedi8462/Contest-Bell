package com.contestBell.baba.Controllers;

import com.contestBell.baba.Repository.UserRepository;
import com.contestBell.baba.Services.GoogleCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
@Slf4j
public class GoogleCalendarController {
    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserId(){
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->  new RuntimeException("User not found!"))
                .getId();
    }

    //user clicks to connect google
    @GetMapping("/connect")
    public ResponseEntity<String> connect(){
        String userId = getCurrentUserId();
        String authUrl = googleCalendarService.generateAuthUrl(userId);
        return new ResponseEntity<>(authUrl, HttpStatus.OK);
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state) {
        try{
            googleCalendarService.handleCallBack(code, state);
            return new ResponseEntity<>("Google Calender Connected successfully!", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Oauth callback failed!", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}