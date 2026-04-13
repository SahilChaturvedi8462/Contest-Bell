package com.contestBell.baba.Controllers;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Repository.ContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/contests")
public class ContestController {
    @Autowired
    private ContestRepository contestRepository;

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingContests() {
        try {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            List<Contest> contests = contestRepository
                    .findByPhaseNotAndStartTimeUtcAfter("FINISHED", now);

            contests.sort(Comparator.comparing(Contest::getStartTimeUtc));

            return new ResponseEntity<>(contests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
