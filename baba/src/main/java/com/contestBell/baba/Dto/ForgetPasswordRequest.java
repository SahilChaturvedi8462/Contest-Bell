package com.contestBell.baba.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ForgetPasswordRequest {
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;
}
