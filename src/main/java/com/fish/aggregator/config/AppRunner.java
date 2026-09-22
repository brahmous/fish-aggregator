package com.fish.aggregator.config;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.fish.aggregator.repository.PostRepository;

@Configuration
public class AppRunner implements ApplicationRunner {

  private final PostRepository postRepository;

  public AppRunner(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  public void run(ApplicationArguments args) throws Exception {

    List<String> queries = args.getOptionValues("queries");
    if (queries == null)
      return;
    for (String queryName : queries) {
      System.out.println("=> : " + queryName);
      if (queryName.equals("getPostsWithMetadataQuery")) {
        System.out.println(MessageFormat.format("QUERY // {0}\n\n [ {1} ]", "getPostsWithMetadataQuery",
            postRepository.getPostsWithMetadataQuery())
            .replace(":limit", "10")
            .replace(":offset", "0")
            .replace(":order_by", "created_at ASC")
            .replace(":account_id", "'11111111-1111-1111-1111-111111111111'"));

      }
    }

  }

}
