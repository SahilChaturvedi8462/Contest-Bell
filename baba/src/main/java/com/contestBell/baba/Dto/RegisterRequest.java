package com.contestBell.baba.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Reqired to enter email")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid Email format"
    )
    private String email;

    @NotBlank(message = "Password Required")
    @Size(min = 6, message = "password must be atleas 6 characters long!")
    private String password;
    private String timeZone;
}
