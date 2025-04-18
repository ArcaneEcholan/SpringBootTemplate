package com.chaowen.springboottemplate.base.module;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * manage app lifecycle and interactions with the platform.
 */
public interface App {

  /**
   * Get the unique name of the app. This is used to identify and register the
   * app.
   *
   * @return the unique name of the app
   */
  @NotNull String getAppName();

  /**
   * retrieve the app specific options.
   */
  @NotNull
  default HashMap<String, String> getAppPreferences() {
    return new HashMap<>();
  }

  /**
   * set app-specific properties provided by the platform. This allows the
   * platform to pass down properties for the app to use.
   *
   * @param json the app-specific properties from the platform
   */
  void setAppConfig(String json);

  /**
   * perform app-specific initialization logic
   */
  void initialize();

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  class AppPreferences {

    NetworkMode networkMode;

    @Deprecated
    String initSql;

    Map<String, Object> options = new HashMap<>();

    public enum NetworkMode {
      NORMAL,
      BRIDGE
    }
  }
}
