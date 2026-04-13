package com.contestBell.baba.Controllers;

import com.contestBell.baba.Dto.UpdateProfileRequest;
import com.contestBell.baba.Dto.UserProfileResponse;
import com.contestBell.baba.Entity.User;
import com.contestBell.baba.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(){
        String email = (String)SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User user = getCurrentUser();
            UserProfileResponse response = UserProfileResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .timeZone(user.getTimezone())
                    .emailVerified(user.isEmailVerified())
                    .createdAt(user.getCreatedAt())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody
                                                UpdateProfileRequest request){
        try {
            User user = getCurrentUser();
            user.setName(request.getName());

            if (request.getTimezone() != null && !request.getTimezone().isEmpty()) {
                // validate timezone
                try {
                    ZoneId.of(request.getTimezone());
                    user.setTimezone(request.getTimezone());
                } catch (Exception e) {
                    return new ResponseEntity<>(
                            "Invalid timezone!", HttpStatus.BAD_REQUEST);
                }
            }

            userRepository.save(user);
            return new ResponseEntity<>("Profile updated!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
