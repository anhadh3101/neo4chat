package com.example.config;

import com.example.model.DateTimeToStringConverter;
import com.example.model.StringToDateTimeConverter;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;

import jakarta.annotation.PreDestroy;
import java.util.Arrays;

@Configuration
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    private Driver driver;

    @Bean
    public Driver neo4jDriver() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        driver.verifyConnectivity();
        return driver;
    }

    @Bean
    public Neo4jConversions neo4jConversions() {
        return new Neo4jConversions(Arrays.asList(
            new DateTimeToStringConverter(),
            new StringToDateTimeConverter()
        ));
    }

    @PreDestroy
    public void closeDriver() {
        if (driver != null) {
            driver.close();
        }
    }
}
