package com.burgerking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.burgerking.membership.repository", "com.burgerking.reservation.repository"})
public class VipBatchOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VipBatchOptimizerApplication.class, args);
    }

}