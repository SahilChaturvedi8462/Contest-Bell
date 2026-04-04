package com.contestBell.baba.Services;

import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Entity.NotificationLog;
import com.contestBell.baba.Entity.Subscription;
import com.contestBell.baba.Entity.User;
import com.contestBell.baba.Repository.ContestRepository;
import com.contestBell.baba.Repository.NotificationLogRepository;
import com.contestBell.baba.Repository.SubscriptionRepository;
import com.contestBell.baba.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private EmailService emailService;

    public void sendNotification(String notificationType,
                                 LocalDateTime from,
                                 LocalDateTime to){

        //find contests starting in given time window
        List<Contest> contests = contestRepository.findByStartTimeUtcBetween(from, to);

        for (Contest contest : contests){

            //find active subscription for this platform
            List<Subscription> subscriptions = subscriptionRepository.findByPlatformAndActiveTrue(
                    contest.getPlatform()
            );

            for (Subscription sub : subscriptions){

                //check if subscription has that division
                if (!sub.getDivisions().contains(contest.getDivision())){
                    continue;
                }

                //check if already notified
                boolean alreadyNotified = notificationLogRepository.existsByUserIdAndContestPlatformIdAndNotificationTypeAndStatus(
                        sub.getUserId(),
                        contest.getContestPlatformId(),
                        notificationType,
                        "SENT"
                );
                if (alreadyNotified)continue;

                // get user
                User user = userRepository.findById(sub.getUserId())
                        .orElse(null);

                if (user == null || !user.isEmailVerified()) continue;


                try{
                    emailService.sendContestNotification(
                            user, contest, notificationType
                    );

                    //log success
                    NotificationLog notificationLog = NotificationLog.builder()
                            .notificationType(notificationType)
                            .userId(sub.getUserId())
                            .contestPlatformId(contest.getContestPlatformId())
                            .platform(contest.getPlatform())
                            .status("SENT")
                            .sentAt(LocalDateTime.now())
                            .build();

                    notificationLogRepository.save(notificationLog);
                } catch (Exception e) {
                    log.error("Failed to notify user {} for contest {}",
                            user.getEmail(), contest.getName(), e);

                    //log failure
                    NotificationLog notificationLog = NotificationLog.builder()
                            .userId(sub.getUserId())
                            .contestPlatformId(contest.getContestPlatformId())
                            .platform(contest.getPlatform())
                            .status("FAIL")
                            .notificationType(notificationType)
                            .sentAt(LocalDateTime.now())
                            .build();

                    notificationLogRepository.save(notificationLog);
                }
            }

        }
    }

}
