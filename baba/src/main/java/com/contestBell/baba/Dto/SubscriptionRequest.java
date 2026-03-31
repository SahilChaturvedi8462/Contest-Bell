package com.contestBell.baba.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SubscriptionRequest {
    @NotBlank(message = "Platform Required..")
    private String platform;

    private List<String> divisions;
}
