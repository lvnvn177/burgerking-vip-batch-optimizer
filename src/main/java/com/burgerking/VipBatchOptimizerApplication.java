package com.burgerking;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableBatchProcessing(
    dataSourceRef = "membershipDataSource",
    transactionManagerRef = "membershipTransactionManager"
)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class VipBatchOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VipBatchOptimizerApplication.class, args);
    }

}