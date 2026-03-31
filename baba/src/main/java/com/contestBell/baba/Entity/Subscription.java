package com.contestBell.baba.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private String id;

    private String userId;
    private String platform;
    private List<String> divisions;
    private boolean active;
    private LocalDateTime createdAt;
}
