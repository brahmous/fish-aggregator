package com.fish.aggregator.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fish.aggregator.repository.CommentRepository.Comment;

@Repository
public class AccountRepository {

  public static record Account(UUID accountId, String username, List<Comment> comments) {
    public Account {
      // comments = List.copyOf(comments);
    }
  };

  private final JdbcClient dbclient;

  public AccountRepository(JdbcClient dbclient) {
    this.dbclient = dbclient;
  }

  public Iterable<Account> getAllAccountsWithComments() {
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