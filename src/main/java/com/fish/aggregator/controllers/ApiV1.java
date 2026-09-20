package com.fish.aggregator.controllers;

import java.text.MessageFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fish.aggregator.repository.PostRepository;
import com.fish.aggregator.repository.PostRepository.LinksOrderBy;
import com.fish.aggregator.repository.PostRepository.Post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * InnerApiV1
 */
@RestController
@RequestMapping("/api/v1")
public class ApiV1 {

  private final PostRepository postrepository;

  public ApiV1(PostRepository postrepository) {
    this.postrepository = postrepository;
  }

  @GetMapping("/links")
  public ResponseEntity<Iterable<Post>> handler(
      @RequestParam(name = "sort") LinksOrderBy linksorderby,
      @RequestParam(name = "offset") @Min(value = 0, message = "cannot be a negative number") int offset,
      @RequestParam(name = "size") @Max(value = 50, message = "maximum is 50") int size) {
    return ResponseEntity.ok()
        .body(
            postrepository
                .getPostsWithMetadata(linksorderby, offset, size));
  }

}
