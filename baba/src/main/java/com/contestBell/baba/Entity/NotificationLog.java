package com.contestBell.baba.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "notification_logs")
public class NotificationLog {
    @Id
    private String id;

    private String userId;
    private String contestId;
    private String notificationType;
    private String platform;
    private String status;
    private LocalDateTime sentAt;
}
