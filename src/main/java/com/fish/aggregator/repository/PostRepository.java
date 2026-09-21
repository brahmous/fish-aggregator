package com.fish.aggregator.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fish.aggregator.repository.AccountRepositoryV1.Account;
import com.fish.aggregator.repository.CommentRepository.Comment;
import com.fish.aggregator.repository.CommentRepository.CommentMetadata;

@Repository
public class PostRepository {

  public static record Upvote() {
  };

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static record Post(
      int postid,
      String title,
      String link,
      Account poster,
      List<Comment> comments,
      List<Upvote> upvotes,
      PostMetadata metadata) {
  };

  public static record PostMetadata(
      Integer commentcount,
      Integer upvotecount) {
  };

  private final JdbcClient dbclient;

  public PostRepository(JdbcClient dbclient) {
    this.dbclient = dbclient;
  }

  public Iterable<Post> getPostsWithMetadata(LinksOrderBy ordering, int offset, int limit) {
    Iterable<Post> posts = dbclient
        .sql(getPostsWithMetadataQuery())
        .param("order_by", "createdat DESC")
        .param("limit", limit)
        .param("offset", offset)
        .param("account_id", "11111111-1111-1111-1111-111111111111")
        .query(PostRepository::getPostsWithMetadataMapper);
    return posts;
  }

  private static Iterable<Post> getPostsWithMetadataMapper(ResultSet rs) throws SQLException {
    Map<Integer, Post> postMap = new HashMap<>();
    Map<UUID, Account> accountMap = new HashMap<>();
    while (rs.next()) {
      int postid = rs.getInt("postid");

      UUID userId = UUID.fromString(rs.getString("accountid"));

      if (!accountMap.containsKey(userId)) {
        accountMap.put(userId,
            new Account(
                userId,
                rs.getString("username"),
                null));
      }

      if (!postMap.containsKey(postid)) {
        Post newPost = new Post(
            postid,
            rs.getString("title"),
            rs.getString("link"),
            accountMap.get(userId),
            new ArrayList<>(),
            null,
            new PostMetadata(
                null,
                rs.getInt("upvote_count")));
        postMap.put(newPost.postid, newPost);
      }

      Post post = postMap.get(postid);
      String comment = rs.getString("comment");
      Boolean hasUpvoted = rs.getBoolean("has_upvoted");

      Account owner = null;
      UUID ownerid = UUID.fromString(rs.getString("commenter_id"));
      if (accountMap.containsKey(ownerid)) {
        owner = accountMap.get(ownerid);
      } else {
        owner = new Account(
            ownerid,
            rs.getString("commenter_username"),
            null);
        accountMap.put(ownerid, owner);
      }

      if (comment != null) {
        post.comments.add(
            new Comment(
                owner,
                comment,
                new CommentMetadata(
                    hasUpvoted != null ? hasUpvoted : false)));
      }

    }
    return postMap.values();
  }

  public String getPostsWithMetadataQuery() {
    // String orderString = "createdat DESC";

    // if (orderBy == LinksOrderBy.ACTIVITY) {
    // TODO: change the quer to order by the articles with the most recent comment
    // data
    // throw new UnsupportedOperationException("Not yet implemented");
    // }

    String SQLTempalte = """
          SELECT
            post.postid,
            post.title,
            u.upvote_count,
            u.has_upvoted,
            CONCAT(domain.domain, post.path) AS link,
            account.username,
            comment.comment,
            post.createdat,
            account.accountid,
            owner.username as commenter_username,
            owner.accountid as commenter_id
          FROM (
            SELECT *
            FROM post
            ORDER BY :order_by
            LIMIT :limit OFFSET :offset
          ) post
          JOIN domain ON domain.domainid = post.domainid
          JOIN account ON account.accountid = post.accountid
          LEFT JOIN LATERAL (
            SELECT
              COUNT(upvote.upvoteid) AS upvote_count,
              COALESCE(BOOL_OR(upvote.accountid = CAST(:account_id AS UUID)), false) AS has_upvoted
            FROM upvote
            WHERE upvote.postid = post.postid
          ) u ON true
          LEFT JOIN comment ON comment.postid = post.postid
          LEFT JOIN account as owner ON comment.ownerid = owner.accountid
          ORDER BY :order_by
        """;

    return SQLTempalte.lines().map(String::trim).collect(Collectors.joining(" "));
  }

  public static enum LinksOrderBy {
    CHRONOLOGICALLY,
    ACTIVITY
  }

  @Component
  public static class LinksOrderByIntConverter implements Converter<String, LinksOrderBy> {

    @Override
    public LinksOrderBy convert(String source) {
      return LinksOrderBy.valueOf(source);
    }

  }
}
