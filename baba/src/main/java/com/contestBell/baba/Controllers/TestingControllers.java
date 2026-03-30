package com.contestBell.baba.Controllers;

import com.contestBell.baba.Services.CodeforcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestingControllers {
    @Autowired
    private CodeforcesService codeforcesService;

    @GetMapping("/cf-fetch")
    public ResponseEntity<String> fetchCf(){
        try{
            codeforcesService.fetchAndSave();
            return new ResponseEntity<>("done fetched and saved", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}