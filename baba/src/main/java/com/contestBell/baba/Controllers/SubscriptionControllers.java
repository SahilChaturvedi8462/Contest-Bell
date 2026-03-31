package com.contestBell.baba.Controllers;

import com.contestBell.baba.Dto.SubscriptionRequest;
import com.contestBell.baba.Entity.Subscription;
import com.contestBell.baba.Repository.UserRepository;
import com.contestBell.baba.Services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
public class SubscriptionControllers {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserRepository userRepository;

    // helper to get current logged in user's id
    private String getCurrentUserId() {
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"))
                .getId();
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@Valid @RequestBody SubscriptionRequest request){
        try{
            subscriptionService.subscribe(getCurrentUserId(), request);
            return new ResponseEntity<>("well done you subscribed to "+ request.getPlatform(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/unsubscribe/{platform}")
    public ResponseEntity<String> unsubscribe(@PathVariable String platform) {
        try {
            subscriptionService.unsubscribe(getCurrentUserId(), platform);
            return new ResponseEntity<>("Unsubscribed successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateDivisions(
            @Valid @RequestBody SubscriptionRequest request) {
        try {
            subscriptionService.updateDivisions(getCurrentUserId(), request);
            return new ResponseEntity<>("Subscription updated!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getMySubscriptions() {
        try {
            return new ResponseEntity<>(
                    subscriptionService.getUserSubscriptions(getCurrentUserId()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
