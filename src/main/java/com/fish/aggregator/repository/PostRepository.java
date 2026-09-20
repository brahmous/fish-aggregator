package com.fish.aggregator.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.fish.aggregator.repository.AccountRepositoryV1.Account;
import com.fish.aggregator.repository.CommentRepository.Comment;

@Repository
public class PostRepository {

  public static record Upvote() {
  };

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
      int commentcount,
      int upvotecount) {
  };

  private final JdbcClient dbclient;

  public PostRepository(JdbcClient dbclient) {
    this.dbclient = dbclient;
  }

  public Iterable<Post> getPostsWithMetadata(LinksOrderBy ordering, int offset, int size) {
    System.out.println("\n\n\n\n" + getPostsWithMetadataQuery() + "\n\n\n\n");
    Iterable<Post> posts = dbclient
        .sql(getPostsWithMetadataQuery())
        .query((rs, rowNumber) -> {
          return new Post(
              rs.getInt("postid"),
              rs.getString("title"),
              rs.getString("link"),
              null,
              null,
              null,
              new PostMetadata(
                  rs.getInt("comment_count"),
                  rs.getInt("upvote_count")));
        }).list();
    return posts;
  }

  /*
   * private static Iterable<Post> getPostsWithMetadataMapper(ResultSet rs) throws
   * SQLException {
   * return List.of(new Post(0, null, null, null, null, null, null));
   * }
   */

  private String getPostsWithMetadataQuery() {
    String SQL = """
        SELECT
          postid,
          title,
          c.comment_count as comment_count,
          u.upvote_count as upvote_count,
          CONCAT(domain.domain, post.path) as link
        FROM post
        LEFT JOIN (
          SELECT postid as comments_post_id, count(comment.commentid
          ) as comment_count FROM comment GROUP BY postid) c ON comments_post_id = post.postid
        LEFT JOIN (
          SELECT postid as upvote_post_id, count(upvote.upvoteid) as upvote_count FROM upvote GROUP BY postid
          ) u ON u.upvote_post_id = post.postid
        JOIN domain ON domain.domainid = post.domainid
        """;
    return SQL.lines().map(String::trim).collect(Collectors.joining(" "));
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
