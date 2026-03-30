package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.CodeforcesContest;
import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Repository.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Slf4j
public class ContestService {
    @Autowired
    private ContestRepository contestRepository;

    public int saveIfNotExists(CodeforcesContest cf) {
        String platformId = String.valueOf(cf.getId());

        //already exists so skip
        if (contestRepository.existsByContestPlatformId(platformId)) {
            return 0;
        }

        // convert epoch seconds to UTC LocalDateTime
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(
                cf.getStartTimeSeconds(),
                0,
                ZoneOffset.UTC
        );

        String division = extractDivision(cf.getName());

        Contest contest = Contest.builder()
                .name(cf.getName())
                .platform("CODEFORCES")
                .division(division)
                .contestUrl("https://codeforces.com/contest/" + cf.getId())
                .contestPlatformId(platformId)
                .startTimeUtc(startTime)
                .durationSeconds(cf.getDurationSeconds())
                .phase(cf.getPhase())
                .build();

        contestRepository.save(contest);
        return 1;
    }

    private String extractDivision(String name) {
        if (name.contains("Div. 1") && name.contains("Div. 2")) return "DIV_1_2";
        if (name.contains("Div. 1")) return "DIV_1";
        if (name.contains("Div. 2")) return "DIV_2";
        if (name.contains("Div. 3")) return "DIV_3";
        if (name.contains("Div. 4")) return "DIV_4";
        if (name.contains("Educational")) return "EDUCATIONAL";
        if (name.contains("Global")) return "GLOBAL";
        return "OTHER";
    }
}