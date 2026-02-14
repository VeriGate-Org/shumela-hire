package com.arthmatic.talentgate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.arthmatic.talentgate", "com.example.recruitment"})
@EntityScan(basePackages = {"com.arthmatic.talentgate", "com.example.recruitment"})
@EnableJpaRepositories(basePackages = {"com.arthmatic.talentgate", "com.example.recruitment"})
public class TalentGateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalentGateApplication.class, args);
    }
}
