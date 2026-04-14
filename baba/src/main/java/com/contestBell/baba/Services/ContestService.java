package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.ClistContest;
import com.contestBell.baba.Dto.CodeforcesContest;
import com.contestBell.baba.Entity.Contest;
import com.contestBell.baba.Repository.ContestRepository;
import com.contestBell.baba.Utils.GetDivision;
import com.contestBell.baba.Utils.PlatformMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ContestService {
    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PlatformMapper platformMapper;

    @Autowired
    private GetDivision getDivision;

    public int saveIfNotExists(CodeforcesContest cf) {
        String platformId = String.valueOf(cf.getId());

        //already exists so skip
        if (contestRepository.existsByContestPlatformId(platformId)) {
            return 0;
        }

        // convert epoch seconds to UTC LocalDateTime
        LocalDateTime startTime = LocalDateTime.ofEpochSecond(
                cf.getStartTimeSeconds(),
                0,
                ZoneOffset.UTC
        );

        String division = getDivision.extractDivision(cf.getName());

        Contest contest = Contest.builder()
                .name(cf.getName())
                .platform("CODEFORCES")
                .division(division)
                .contestUrl("https://codeforces.com/contest/" + cf.getId())
                .contestPlatformId(platformId)
                .startTimeUtc(startTime)
                .durationSeconds(cf.getDurationSeconds())
                .phase(cf.getPhase())
                .build();

        contestRepository.save(contest);

        // notify users about new contest
        notificationService.sendNotification(
                "NEW_CONTEST",
                contest.getStartTimeUtc().minusYears(1),
                contest.getStartTimeUtc().plusYears(1)
        );

        return 1;
    }

    public int saveIfNotExistsClist(ClistContest cc, String resource){
        String platformId = "CLIST_" + cc.getId();

        if (contestRepository.existsByContestPlatformId(platformId)) {
            return 0;
        }

        LocalDateTime startTime = LocalDateTime.parse(cc.getStart(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();


        String platform = platformMapper.getPlatformName(resource);
        String division = getDivision.extractDivision(cc.getEvent());

        Contest contest = Contest.builder()
                .name(cc.getEvent())
                .platform(platform)
                .division(division)
                .contestUrl(cc.getHref())
                .contestPlatformId(platformId)
                .startTimeUtc(startTime)
                .durationSeconds((int)cc.getDuration())
                .phase("BEFORE")
                .build();

        contestRepository.save(contest);
        // notify users about new contest
        notificationService.sendNotification(
                "NEW_CONTEST",
                contest.getStartTimeUtc().minusYears(1),
                contest.getStartTimeUtc().plusYears(1)
        );

        return 1;
    }

}