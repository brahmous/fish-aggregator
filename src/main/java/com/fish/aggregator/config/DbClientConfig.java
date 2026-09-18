package com.fish.aggregator.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
class DBClientConfig {

  private final PostgresConfig dbconfig;

  public DBClientConfig(PostgresConfig dbconfig) {
    this.dbconfig = dbconfig;
  }

  @Bean
  DataSource configureDataSource() {
    HikariDataSource datasource = new HikariDataSource();
    datasource.setJdbcUrl(dbconfig.url());
    datasource.setUsername(dbconfig.username());
    datasource.setPassword(dbconfig.password());
    return datasource;
  }

  @Bean
  JdbcClient configureJdbcClient(DataSource datasource) {
    return JdbcClient.create(datasource);
  }

}
