package it.almaviva.difesa.cessazione.auth.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager",
        basePackages = {"it.almaviva.difesa.cessazione.auth.data.repository"})
public class AuthDbConfiguration {

    @Value("${spring.liquibase.enabled}")
    private boolean enabled;

    @Primary
    @Bean(name = "authDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "authDataSource")
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource authDataSource(@Qualifier("authDataSourceProperties") DataSourceProperties authDataSourceProperties) {
        return authDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder authEntityManagerFactoryBuilder,
            @Qualifier("authDataSource") DataSource authDataSource,
            ConfigurableEnvironment env) {

        Map<String, String> authJpaProperties = new HashMap<>();
        authJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL91Dialect");

        return authEntityManagerFactoryBuilder
                .dataSource(authDataSource)
                .packages("it.almaviva.difesa.cessazione.auth.data.entity")
                .persistenceUnit("authDataSource")
                .properties(authJpaProperties)
                .build();
    }

    @Primary
    @Bean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory authEntityManagerFactory) {

        return new JpaTransactionManager(authEntityManagerFactory);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase")
    public LiquibaseProperties authLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public SpringLiquibase authLiquibase(@Qualifier("authDataSourceProperties") DataSourceProperties authDataSourceProperties) {
        return springLiquibase(authDataSource(authDataSourceProperties), authLiquibaseProperties());
    }

    private SpringLiquibase springLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        if (enabled) {
            var liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog(properties.getChangeLog());
            liquibase.setContexts(properties.getContexts());
            liquibase.setDefaultSchema(properties.getDefaultSchema());
            liquibase.setDropFirst(properties.isDropFirst());
            liquibase.setShouldRun(properties.isEnabled());
            liquibase.setLabels(properties.getLabels());
            liquibase.setChangeLogParameters(properties.getParameters());
            liquibase.setRollbackFile(properties.getRollbackFile());
            return liquibase;
        }
        return null;
    }
}
