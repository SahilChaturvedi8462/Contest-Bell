package com.contestBell.baba.Repository;

import com.contestBell.baba.Entity.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    boolean existsByUserIdAndContestPlatformIdAndNotificationTypeAndStatus(String userId,
                                                                           String contestPlatformId,
                                                                           String notificationType,
                                                                           String status);
}
