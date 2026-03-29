package com.contestBell.baba.Controllers;

import com.contestBell.baba.Dto.RegisterRequest;
import com.contestBell.baba.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request){
        try{
            userService.register(request);
            return new ResponseEntity<>("We sent you email please check!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        try{
            userService.verifyEmail(token);
            return new ResponseEntity<>("Welcome!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
