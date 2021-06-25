package com.carrotins.boot.jdbc.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    value = ["com.carrotins.boot.jdbc.repository"],
    transactionManagerRef = "transactionManager",
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean::class)
@EnableJpaAuditing
class PersistenceConfig
