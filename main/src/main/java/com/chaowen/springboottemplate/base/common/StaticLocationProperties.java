package com.chaowen.springboottemplate.base.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "static")
public class StaticLocationProperties {

  private List<Location> locations = new ArrayList<>();

  public enum LocationType {
    CLASSPATH,
    FS;

    @JsonCreator
    public static LocationType from(String value) {
      return LocationType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
      return name().toLowerCase();
    }
  }

  @Data
  public static class Location {

    private LocationType type;
    private String path;
  }
}
