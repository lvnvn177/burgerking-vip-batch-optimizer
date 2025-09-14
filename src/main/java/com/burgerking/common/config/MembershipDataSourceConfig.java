package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.burgerking.membership.repository",
    entityManagerFactoryRef = "membershipEntityManagerFactory",
    transactionManagerRef = "membershipTransactionManager"
)
public class MembershipDataSourceConfig {

    @Primary
    @Bean(name = "membershipDataSource")
    public DataSource membershipDataSource(CustomDataSourceProperties properties) {
        return properties.getMembership().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "membershipEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean membershipEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("membershipDataSource") DataSource dataSource) {
        return builder
            .dataSource(dataSource)
            .packages("com.burgerking.membership.domain")
            .persistenceUnit("membership")
            .build();
    }

    @SuppressWarnings("null")
    @Primary
    @Bean(name = "membershipTransactionManager")
    public PlatformTransactionManager membershipTransactionManager(
        @Qualifier("membershipEntityManagerFactory") LocalContainerEntityManagerFactoryBean membershipEntityManagerFactory) {
        return new JpaTransactionManager(membershipEntityManagerFactory.getObject());
    }
}