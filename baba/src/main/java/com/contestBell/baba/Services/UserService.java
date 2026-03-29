package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.RegisterRequest;
import com.contestBell.baba.Entity.User;
import com.contestBell.baba.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request){
        try {
            if (userRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException("Email alredy exists!");
            }

            String token = UUID.randomUUID().toString();

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .emailVerified(false)
                    .verificationToken(token)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            emailService.sendVerificationMail(request.getEmail(), token);
        } catch (Exception e) {
            log.error("failed to register", e);
        }
    }

    public void verifyEmail(String token){
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("invalid Token!"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}
