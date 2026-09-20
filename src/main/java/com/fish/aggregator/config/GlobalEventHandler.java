package com.fish.aggregator.config;

import java.text.MessageFormat;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fish.aggregator.repository.AccountRepositoryV1;

@Configuration
public class GlobalEventHandler {

  private final AccountRepositoryV1 accountrepo;

  public GlobalEventHandler(AccountRepositoryV1 accountrepo) {
    this.accountrepo = accountrepo;
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> applicationReadyEventHandler() {
    return (event) -> {
    };
  }
}