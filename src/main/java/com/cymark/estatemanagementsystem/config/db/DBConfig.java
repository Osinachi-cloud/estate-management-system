//package com.cymark.estatemanagementsystem.config.db;
//
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
///**
// * Configuration class for setting up the Postgres database connection.
// */
//@Configuration
//@EnableTransactionManagement
//@RequiredArgsConstructor
//
//public class DBConfig {
//
//    private static final Logger log = LoggerFactory.getLogger(DBConfig.class);
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    @Value("${spring.datasource.url}")
//    private String url;
//
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Primary
//    @Bean
//    @ConfigurationProperties("postgres.datasource")
//    public DataSourceProperties datasourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Primary
//    @Bean("dataSource")
//    public DataSource dataSource() {
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(url);
//        ds.setUsername(username);
//        ds.setDriverClassName("org.postgresql.Driver");
//        ds.setPassword(password);
//        return ds;
//    }
//
//    @Primary
//    @Bean("jdbcTemplate")
//    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") final DataSource datasource) {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate();
//        jdbcTemplate.setDataSource(datasource);
//        jdbcTemplate.setResultsMapCaseInsensitive(true);
//        return jdbcTemplate;
//    }
//
//    @Primary
//    @Bean(name = "realEntityManager")
//    public LocalContainerEntityManagerFactoryBean realEntityManager(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(dataSource())
//                .packages("com.cymark.estatemanagementsystem.model.entity")
//                .persistenceUnit("entity")
//                .build();
//    }
//
//    /**
//     * Configures a transaction manager for the postgres data source.
//     *
//     * @param entityManagerFactory EntityManagerFactory instance for postgres.
//     * @return JpaTransactionManager instance.
//     */
//    @Primary
//    @Bean(name = "transactionManager")
//    public JpaTransactionManager transactionManager(
//            @Qualifier("realEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory.getObject());
//    }
//}
//
//
