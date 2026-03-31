package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.SubscriptionRequest;
import com.contestBell.baba.Entity.Subscription;
import com.contestBell.baba.Repository.SubscriptionRepository;
import com.contestBell.baba.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public void subscribe(String userId, SubscriptionRequest request){
        if (subscriptionRepository.existsByUserIdAndPlatform(userId, request.getPlatform())){
            throw new RuntimeException("Already Subscribed to "+ request.getPlatform());
        }

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .platform(request.getPlatform())
                .divisions(request.getDivisions())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(String userId, String platform){
        Subscription subscription = subscriptionRepository.findByUserIdAndPlatform(
                userId,
                platform
        ).orElseThrow(() -> new RuntimeException("Subscription not found!"));

        subscription.setActive(false);
        subscriptionRepository.save(subscription);
    }

    public void updateDivisions(String userId, SubscriptionRequest request){
        Subscription subscription = subscriptionRepository
                .findByUserIdAndPlatform(userId, request.getPlatform())
                .orElseThrow(() -> new RuntimeException("Subscription not found!"));

        subscription.setDivisions(request.getDivisions());
        subscription.setActive(true);
        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(String userId){
        return subscriptionRepository.findByUserId(userId);
    }
}
