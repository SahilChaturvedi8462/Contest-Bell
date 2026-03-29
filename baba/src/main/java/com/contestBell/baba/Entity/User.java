package com.contestBell.baba.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@Builder
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String name;
    private String password;
    private boolean emailVerified = false;
    private String verificationToken;
    private LocalDateTime createdAt;
}
