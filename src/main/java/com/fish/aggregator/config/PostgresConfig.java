package com.fish.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.fish.aggregator.postgresconfig")
record PostgresConfig(String url, String username, String password) {
};