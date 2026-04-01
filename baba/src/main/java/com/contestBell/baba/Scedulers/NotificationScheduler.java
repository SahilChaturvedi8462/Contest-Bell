package com.contestBell.baba.Scedulers;

import com.contestBell.baba.Services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Slf4j
public class NotificationScheduler {
    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void sendDayBefore(){
        log.info("Checking 24 hour notifications...");
        LocalDateTime from = LocalDateTime.now(ZoneOffset.UTC).plusHours(23);
        LocalDateTime to = LocalDateTime.now(ZoneOffset.UTC).plusHours(25);
        notificationService.sendNotification("DAY_BEFORE", from, to);
    }

    //for hour before every 10 min
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void sendHourBefore(){
        log.info("Checking 1 hour notifications...");
        LocalDateTime from = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(45);
        LocalDateTime to = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(75);
        notificationService.sendNotification("HOUR_BEFORE", from, to);
    }

}
