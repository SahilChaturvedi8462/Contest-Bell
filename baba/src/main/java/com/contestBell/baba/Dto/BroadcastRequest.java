package com.contestBell.baba.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BroadcastRequest {
    @NotBlank
    private String subject;
    @NotBlank
    private String message;
}
