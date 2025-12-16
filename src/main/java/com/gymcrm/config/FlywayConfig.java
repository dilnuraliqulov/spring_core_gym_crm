package com.gymcrm.config;


import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Value("${flyway.locations}")
    private String flywayLocations;

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayLocations)
                .baselineOnMigrate(true)
                .load();
        // Repair to fix checksum mismatch from modified migration files
        flyway.repair();
        flyway.migrate();
        return flyway;
    }

}
