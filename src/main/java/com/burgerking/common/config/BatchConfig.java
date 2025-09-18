package com.burgerking.common.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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
    protected DataSource getDataSource() {
        return this.membershipDataSource;
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return this.membershipTransactionManager;
    }
}