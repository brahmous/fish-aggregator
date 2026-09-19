package com.fish.aggregator.controllers;

import java.text.MessageFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fish.aggregator.controllers.helpers.Helpers.LinksOrderBy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * InnerApiV1
 */
@RestController
@RequestMapping("/api/v1")
public class ApiV1 {

  // /api/v1/posts?sort=2&start=0&limit=20'
  @GetMapping("/links")
  public ResponseEntity<String> handler(
      @RequestParam(name = "sort") LinksOrderBy linksorderby,
      @RequestParam(name = "offset") @Min(value = 0, message = "cannot be a negative number") int offset,
      @RequestParam(name = "size") @Max(value = 50, message = "maximum is 50") int size) {
    return ResponseEntity.ok()
        .body(MessageFormat.format("orderby: {0} // offset: {1} // size: {2}", linksorderby, offset, size));
  }

}
