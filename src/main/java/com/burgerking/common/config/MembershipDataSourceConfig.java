package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    @Bean(name = "membershipProperties")
    @ConfigurationProperties("spring.datasource.membership")
    public DataSourceProperties membershipProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "membershipDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.membership")
    public DataSource membershipDataSource(@Qualifier("membershipProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

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
    @Bean(name = "membershipTransactionManager")
    public PlatformTransactionManager membershipTransactionManager(
        @Qualifier("membershipEntityManagerFactory") LocalContainerEntityManagerFactoryBean membershipEntityManagerFactory) {
        return new JpaTransactionManager(membershipEntityManagerFactory.getObject());
    }
}