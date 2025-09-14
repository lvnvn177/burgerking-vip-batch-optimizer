package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.burgerking.reservation.repository",
    entityManagerFactoryRef = "reservationEntityManagerFactory",
    transactionManagerRef = "reservationTransactionManager"
)
public class ReservationDataSourceConfig {

    @Bean(name = "reservationProperties")
    @ConfigurationProperties("spring.datasource.reservation")
    public DataSourceProperties reservationProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "reservationDataSource")
    public DataSource reservationDataSource(@Qualifier("reservationProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "reservationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reservationEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("reservationDataSource") DataSource dataSource) {
        return builder
            .dataSource(dataSource)
            .packages("com.burgerking.reservation.domain")
            .persistenceUnit("reservation")
            .build();
    }

    @SuppressWarnings("null")
    @Bean(name = "reservationTransactionManager")
    public PlatformTransactionManager reservationTransactionManager(
        @Qualifier("reservationEntityManagerFactory") LocalContainerEntityManagerFactoryBean reservationEntityManagerFactory) {
        return new JpaTransactionManager(reservationEntityManagerFactory.getObject());
    }
}