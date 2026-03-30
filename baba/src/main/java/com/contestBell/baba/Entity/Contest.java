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
@Document(collection = "contests")
@Builder
public class Contest {
    @Id
    private String id;

    private String name;
    private String platform;
    private String division;
    private String contestUrl;
    private String contestPlatformId;
    private LocalDateTime startTimeUtc;
    private int durationSeconds;
    private String phase;
}
