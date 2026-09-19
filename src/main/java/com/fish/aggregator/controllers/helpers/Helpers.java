package com.fish.aggregator.controllers.helpers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

public class Helpers {
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
