package com.fish.aggregator.repository;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

import com.fish.aggregator.repository.AccountRepositoryV1.Account;

@Repository
public class CommentRepository {
  public static record Comment(Account owner, String comment, CommentMetadata metadata, OffsetDateTime created_at) {
  }

  public static record CommentMetadata(boolean upvoted) {
  }
}
