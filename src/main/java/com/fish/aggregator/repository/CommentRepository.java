package com.fish.aggregator.repository;

import org.springframework.stereotype.Repository;

import com.fish.aggregator.repository.AccountRepository.Account;

@Repository
public class CommentRepository {
  public static record Comment(Account owner, String comment) {
  }
}
