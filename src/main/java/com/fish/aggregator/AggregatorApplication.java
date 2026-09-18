package com.fish.aggregator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Repository;

import com.zaxxer.hikari.HikariDataSource;

record Comment(Account owner, String comment) {
};

record Account(UUID accountId, String username, List<Comment> comments) {
	public Account {
		// comments = List.copyOf(comments);
	}
};

@Repository
class AccountRepository {

	private final JdbcClient dbclient;

	public AccountRepository(JdbcClient dbclient) {
		this.dbclient = dbclient;
	}

	Iterable<Account> getAllAccountsWithComments() {
		return dbclient
				.sql("SELECT * FROM account JOIN comment ON account_id = ownerid;")
				.query(AccountRepository::getAllAccountWithCommentsMapper);
	}

	private static Iterable<Account> getAllAccountWithCommentsMapper(ResultSet rs) throws SQLException {
		Map<String, Account> map = new HashMap<>();
		while (rs.next()) {
			String account_uuid = rs.getString("account_id");
			if (!map.containsKey(account_uuid)) {
				Account account = new Account(UUID.fromString(account_uuid), rs.getString("username"),
						new ArrayList<>());
				account.comments().add(new Comment(account, rs.getString("comment")));
				map.put(account_uuid, account);
			} else {
				Account account = map.get(rs.getString(account_uuid));
				Comment comment = new Comment(account, rs.getString("comment"));
				account.comments().add(comment);
			}
		}
		return map.values();
	}

}

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

	private final AccountRepository accountrepo;

	public GlobalEventHandler(AccountRepository accountrepo) {
		this.accountrepo = accountrepo;
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> applicationReadyEventHandler() {
		return (event) -> {
			Iterable<Account> accounts = accountrepo.getAllAccountsWithComments();
			accounts.forEach((account) -> {
				System.out.println(MessageFormat.format("[id: {0}, username: {1}]", account.accountId(), account.username()));
				account.comments().forEach(comment -> {
					System.out.println(comment.comment());
				});
			});
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
