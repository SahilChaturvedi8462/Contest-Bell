package com.contestBell.baba.Scedulers;

import com.contestBell.baba.Services.ClistService;
import com.contestBell.baba.Services.CodeforcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FetchContests {
    @Autowired
    private CodeforcesService codeforcesService;

    @Autowired
    private ClistService clistService;

    //every 3 hours
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    public void fetchCodeforces(){
        log.info("Scheduler triggered- fetching contests..");
        codeforcesService.fetchAndSave();
    }

    @Scheduled(fixedRate = 4 * 60 * 60 * 1000)
    public void fetchClistContests() {
        log.info("Fetching contests from clist.by...");
        clistService.fetchAndSave("codechef.com");
        clistService.fetchAndSave("leetcode.com");
        clistService.fetchAndSave("atcoder.jp");
        clistService.fetchAndSave("hackerrank.com");
    }
}
