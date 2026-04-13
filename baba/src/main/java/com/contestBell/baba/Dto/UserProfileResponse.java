package com.contestBell.baba.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String timeZone;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}
