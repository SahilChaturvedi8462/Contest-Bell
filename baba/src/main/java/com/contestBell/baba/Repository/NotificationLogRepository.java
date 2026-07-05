package com.contestBell.baba.Repository;

import com.contestBell.baba.Entity.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    boolean existsByUserIdAndContestPlatformIdAndNotificationTypeAndStatus(String userId,
                                                                           String contestPlatformId,
                                                                           String notificationType,
                                                                           String status);
    List<NotificationLog> findByContestPlatformIdIn(List<String> contestPlatformIds);
}
