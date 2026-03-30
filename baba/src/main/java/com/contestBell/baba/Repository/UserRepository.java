package com.contestBell.baba.Repository;

import com.contestBell.baba.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByEmail(String email);
}
