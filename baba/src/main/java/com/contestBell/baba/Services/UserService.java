package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.ForgetPasswordRequest;
import com.contestBell.baba.Dto.LoginRequest;
import com.contestBell.baba.Dto.RegisterRequest;
import com.contestBell.baba.Dto.ResetPasswordRequest;
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

    @Autowired
    private JwtService jwtService;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        String token = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .verificationToken(token)
                .timezone(request.getTimeZone())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        emailService.sendVerificationMail(request.getEmail(), token);

    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("invalid Token!"));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account with this email not found! please register first"));
        if (user.isEmailVerified()) {
            throw new RuntimeException("You are already verified go and log-in!");
        }

        String newToken = UUID.randomUUID().toString();

        user.setVerificationToken(newToken);
        userRepository.save(user);
        emailService.sendVerificationMail(email, newToken);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email or Password!"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password! or try forget password!");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public void forgetPassword(ForgetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));


        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first!");
        }

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailService.sendPasswordResetMail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("User not found!"));


        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetTokenExpiry(null);
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }

    public void validateResetPasswordToken(String token) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired. Please request a new one!");
        }
    }
}
