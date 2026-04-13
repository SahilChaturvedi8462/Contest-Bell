package com.contestBell.baba.Repository;

import com.contestBell.baba.Entity.Contest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestRepository extends MongoRepository<Contest, String> {
    boolean existsByContestPlatformId(String contestPlatformId);
    List<Contest> findByPlatformAndPhase(String platform, String phase);
    List<Contest> findByStartTimeUtcBetween(LocalDateTime start, LocalDateTime end);
    List<Contest> findByPhaseNotAndStartTimeUtcAfter(
            String phase, LocalDateTime after);
    List<Contest> findByPhaseAndStartTimeUtcBefore(String phase, LocalDateTime before);
}
