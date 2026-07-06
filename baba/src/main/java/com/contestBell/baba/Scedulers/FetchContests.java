package com.contestBell.baba.Scedulers;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Entity.NotificationLog;
import com.contestBell.baba.Repository.ContestRepository;
import com.contestBell.baba.Repository.NotificationLogRepository;
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

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    //every 3 hours
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000, initialDelay = 0)
    public void fetchCodeforces() {
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

    //cleaing old notification logs
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUpOldNotificationLog() {
        //find contest ended before 3 days
        LocalDateTime threeDaysAgo = LocalDateTime.now(ZoneOffset.UTC).minusDays(3);

        List<Contest> oldContest = contestRepository
                .findByStartTimeUtcBefore(threeDaysAgo); 

        if (oldContest.isEmpty()) {
            log.info("No old notification log to cleanUp");
            return;
        }

        //get thire platform id's
        List<String> oldPlatformIds = oldContest.stream()
                .map(Contest::getContestPlatformId)
                .toList();

        //delete thire logs
        List<NotificationLog> oldLogs = notificationLogRepository.findByContestPlatformIdIn(oldPlatformIds);

        notificationLogRepository.deleteAll(oldLogs);
        log.info("clean up {} old notification logs", oldLogs.size());

        // then delete contests
        contestRepository.deleteAll(oldContest);
        log.info("Cleaned up {} old contests", oldContest.size());
    }
}