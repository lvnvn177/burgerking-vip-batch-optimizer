package com.burgerking.common.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasources")
public class CustomDataSourceProperties {

    private DataSourceProperties membership = new DataSourceProperties();
    private DataSourceProperties reservation = new DataSourceProperties();

    public DataSourceProperties getMembership() {
        return membership;
    }

    public void setMembership(DataSourceProperties membership) {
        this.membership = membership;
    }

    public DataSourceProperties getReservation() {
        return reservation;
    }

    public void setReservation(DataSourceProperties reservation) {
        this.reservation = reservation;
    }
}