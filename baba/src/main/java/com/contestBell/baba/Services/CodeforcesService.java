package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.CodeForcesResponse;
import com.contestBell.baba.Dto.CodeforcesContest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CodeforcesService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ContestService contestService;

    private static final String CODEFORCES_API =
            "https://codeforces.com/api/contest.list?gym=false";

    public void fetchAndSave(){
        try{
            log.info("Fetching Codeforces contests.....");

            CodeForcesResponse response = restTemplate.getForObject(
                    CODEFORCES_API,
                    CodeForcesResponse.class
            );

            if (response == null && !"OK".equals(response.getStatus())){
                log.error("Failed to fetch data from Codeforces API");
                return;
            }

            int saved = 0;
            for(CodeforcesContest cf : response.getResult()){
                if ("FINISHED".equals(cf.getPhase()))continue;

                saved += contestService.saveIfNotExists(cf);
            }
            log.info("Done. Saved {} new Contests.", saved);
        } catch (Exception e) {
            log.error("Error fetching Codeforces contests!", e);
        }
    }
}
