package com.contestBell.baba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class BabaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BabaApplication.class, args);
	}

    @Bean
    public RestTemplate responseTemplate(){
        return new RestTemplate();
    }
}
