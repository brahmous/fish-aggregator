package com.fish.aggregator.config;

import java.text.MessageFormat;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fish.aggregator.repository.AccountRepository;

@Configuration
public class GlobalEventHandler {

  private final AccountRepository accountrepo;

  public GlobalEventHandler(AccountRepository accountrepo) {
    this.accountrepo = accountrepo;
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> applicationReadyEventHandler() {
    return (event) -> {
      Iterable<AccountRepository.Account> accounts = accountrepo.getAllAccountsWithComments();
      accounts.forEach((account) -> {
        System.out.println(MessageFormat.format("[id: {0}, username: {1}]",
            account.accountId(), account.username()));
        account.comments().forEach(comment -> {
          System.out.println(comment.comment());
        });
      });
    };
  }
}