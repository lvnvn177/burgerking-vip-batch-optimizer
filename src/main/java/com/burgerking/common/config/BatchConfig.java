package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import io.micrometer.common.lang.NonNull;

@Configuration
public class BatchConfig extends DefaultBatchConfiguration {

    private final DataSource membershipDataSource;
    private final PlatformTransactionManager membershipTransactionManager;

    public BatchConfig(
            @Qualifier("membershipDataSource") DataSource membershipDataSource,
            @Qualifier("membershipTransactionManager") PlatformTransactionManager membershipTransactionManager) {
        this.membershipDataSource = membershipDataSource;
        this.membershipTransactionManager = membershipTransactionManager;
    }

    @Override
    @NonNull
    protected DataSource getDataSource() {
        if (this.membershipDataSource == null) {
             throw new IllegalStateException("membershipDataSource must not be null");
        }
        return this.membershipDataSource;
    }

    @Override
    @NonNull
    protected PlatformTransactionManager getTransactionManager() {
        if (this.membershipTransactionManager == null) {
            throw new IllegalStateException("mumbershipTransaction Manager must not be null");
        }
        return this.membershipTransactionManager;
    }
}