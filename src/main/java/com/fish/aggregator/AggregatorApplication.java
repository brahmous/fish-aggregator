package com.fish.aggregator;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.zaxxer.hikari.HikariDataSource;

record Account(UUID accountId, String username) {
};

@ConfigurationProperties(prefix = "com.fish.aggregator.postgresconfig")
record PostgresConfig(String url, String username, String password) {
};

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

@Configuration
class GlobalEventHandler {

	private final JdbcClient dbclient;

	public GlobalEventHandler(JdbcClient dbclient) {
		this.dbclient = dbclient;
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> applicationReadyEventHandler() {
		return (event) -> {
			List<Account> accounts = dbclient.sql("SELECT * FROM account").query(Account.class).list();
			for (Account account : accounts) {
				System.out.println(account);
			}
		};
	}

}

@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(AggregatorApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
		// SpringApplication.run(AggregatorApplication.class, args);
	}

}
