package com.contestBell.baba.Repository;

import com.contestBell.baba.Entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByUserId(String userId);
    Optional<Subscription> findByUserIdAndPlatform(String userId, String platform);
    List<Subscription> findByPlatformAndActiveTrue(String platform);
    boolean existsByUserIdAndPlatform(String userId, String platform);
}
