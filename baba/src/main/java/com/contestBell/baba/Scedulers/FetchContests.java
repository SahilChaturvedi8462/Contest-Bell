package com.contestBell.baba.Scedulers;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Repository.ContestRepository;
import com.contestBell.baba.Services.ClistService;
import com.contestBell.baba.Services.CodeforcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Slf4j
public class FetchContests {
    @Autowired
    private CodeforcesService codeforcesService;

    @Autowired
    private ClistService clistService;

    @Autowired
    private ContestRepository contestRepository;

    //every 3 hours
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000, initialDelay = 0)
    public void fetchCodeforces(){
        log.info("Scheduler triggered- fetching contests..");
        codeforcesService.fetchAndSave();
    }

    @Scheduled(fixedRate = 4 * 60 * 60 * 1000, initialDelay = 5000)
    public void fetchClistContests() {
        log.info("Fetching contests from clist.by...");
        clistService.fetchAndSave("codechef.com");
        clistService.fetchAndSave("leetcode.com");
        clistService.fetchAndSave("atcoder.jp");
        clistService.fetchAndSave("hackerrank.com");
    }

    @Scheduled(cron = "0 0 2 * * *") // runs at 2 AM every day
    public void cleanupFinishedContests() {
        LocalDateTime threeDaysAgo = LocalDateTime.now(ZoneOffset.UTC).minusDays(3);
        List<Contest> old = contestRepository
                .findByPhaseAndStartTimeUtcBefore("FINISHED", threeDaysAgo);
        contestRepository.deleteAll(old);
        log.info("Cleaned up {} old contests", old.size());
    }
}
