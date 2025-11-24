package com.gymcrm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class HibernateConfig {

    // Load YAML properties
    private final DatabaseProperties dbProps;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.packages-to-scan}")
    private String packagesToScan;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbProps.getDriverClassName());
        dataSource.setUrl(dbProps.getUrl());
        dataSource.setUsername(dbProps.getUsername());
        dataSource.setPassword(dbProps.getPassword());
        return dataSource;
    }


    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(packagesToScan);

        Properties props = new Properties();
        props.setProperty("hibernate.dialect", dialect);
        props.setProperty("hibernate.show_sql", showSql);
        sessionFactory.setHibernateProperties(props);

        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(LocalSessionFactoryBean sessionFactory) {
        return new HibernateTransactionManager(sessionFactory.getObject());
    }

}
