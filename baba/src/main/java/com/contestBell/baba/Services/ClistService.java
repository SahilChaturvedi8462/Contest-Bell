package com.contestBell.baba.Services;

import com.contestBell.baba.Dto.ClistContest;
import com.contestBell.baba.Dto.ClistResponse;
import com.contestBell.baba.Utils.PlatformMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ClistService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ContestService contestService;

    @Autowired
    private PlatformMapper platformMapper;

    @Value("${clist.api-key}")
    private String apiKey;

    @Value("${clist.username}")
    private String username;

    private static final String CLIST_API =
            "https://clist.by/api/v4/contest/";

    public void fetchAndSave(String resource){
        try{
            log.info("Fetching contests from clis.by of {}", resource);

            String url = CLIST_API + "?resource=" + resource
                    + "&upcoming=true"
                    + "&limit=10"
                    + "&format=json"
                    + "&username=" + username
                    + "&api_key=" + apiKey;

            ClistResponse response = restTemplate.getForObject(url, ClistResponse.class);

            if (response == null || response.getObjects() == null){
                log.error("No response from clist.by for {}", resource);
                return;
            }

            int saved = 0;
            for (ClistContest cc : response.getObjects()) {
                saved += contestService.saveIfNotExistsClist(cc, resource);
            }

            log.info("Clist fetch complete for {}. Saved {} new contests.",
                    resource, saved);

        } catch (Exception e) {
            log.error("Error fetching from clist.by for {}", resource, e);
        }
    }
}
