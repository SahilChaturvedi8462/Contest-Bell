package com.contestBell.baba.Controllers;

import com.contestBell.baba.Dto.ForgetPasswordRequest;
import com.contestBell.baba.Dto.LoginRequest;
import com.contestBell.baba.Dto.RegisterRequest;
import com.contestBell.baba.Dto.ResetPasswordRequest;
import com.contestBell.baba.Services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return new ResponseEntity<>("We sent you email please check!", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return new ResponseEntity<>("Welcome!", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        try {
            userService.resendVerificationEmail(email);
            return new ResponseEntity<>("Please check your email!", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        try{
            String token = userService.login(request);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody
                                                     ForgetPasswordRequest request){
        try {
            userService.forgetPassword(request);
            return new ResponseEntity<>("Password reset email sent. Please check your inbox.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> validateResetToken(@RequestParam String token) {
        try {
            userService.validateResetPasswordToken(token);
            return new ResponseEntity<>("Token valid. Submit your new password.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return new ResponseEntity<>("Password reset successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
