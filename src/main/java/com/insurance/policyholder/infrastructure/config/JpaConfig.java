package com.insurance.policyholder.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 配置
 * 啟用 JPA 相關功能
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.insurance.policyholder.infrastructure.adapter.output.persistence.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // JPA 配置類，目前使用預設配置
    // 可在此加入自訂的 EntityManager、DataSource 等配置
}
