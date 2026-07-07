package com.contestBell.baba.Controllers;

import com.contestBell.baba.Dto.BroadcastRequest;
import com.contestBell.baba.Entity.User;
import com.contestBell.baba.Repository.UserRepository;
import com.contestBell.baba.Services.EmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.admin-secret}")
    private String adminSecret;

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(
            @RequestHeader("X-ADMIN-SECRET") String secret,
            @Valid @RequestBody BroadcastRequest request
    ) {
        if(!adminSecret.equals(secret)){
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        List<User> users = userRepository.findAll();
        int sent = 0;
        for(User user : users){
            try{
                emailService.sendBroadCastMail(
                        user.getEmail(),
                        user.getName(),
                        request.getSubject(),
                        request.getMessage()
                );
                sent++;
            } catch (Exception e) {
                log.error("Failed to send broadcast email to {}", user.getEmail(), e);
            }
        }
        return ResponseEntity.ok("Broadcast sent to " + sent + " users:)");
    }

}
