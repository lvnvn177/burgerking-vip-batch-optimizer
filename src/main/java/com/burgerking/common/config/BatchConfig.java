package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public JobRepository jobRepository(
        @Qualifier("membershipDataSource") DataSource dataSource,
        @Qualifier("membershipTransactionManager") 
        PlatformTransactionManager transactionManager
    ) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
    @Bean
    public JobExplorer jobExplorer(
        @Qualifier("membershipDataSource") DataSource dataSource,
        @Qualifier("membershipTransactionManager")
        PlatformTransactionManager transactionManager
        ) throws Exception {
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
    @Bean
    public JobOperator jobOperator(
        JobLauncher jobLauncher,
        JobRepository jobRepository,
        JobExplorer jobExplorer,
        JobRegistry jobRegistry,
        @Qualifier("membershipTransactionManager") PlatformTransactionManager transactionManager
    ) throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }
}