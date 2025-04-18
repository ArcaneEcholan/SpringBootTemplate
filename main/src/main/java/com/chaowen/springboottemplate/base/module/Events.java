package com.chaowen.springboottemplate.base.module;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

public class Events {

  public static class AfterNetworkSetup extends ApplicationEvent {

    public AfterNetworkSetup(Object source) {
      super(source);
    }
  }

  @Getter
  public static class CenterPublicAccessUpdatedEvent extends ApplicationEvent {

    private final Object publicAccess;

    public CenterPublicAccessUpdatedEvent(Object source, Object publicAccess) {
      super(source);
      this.publicAccess = publicAccess;
    }
  }

  @Data
  @NoArgsConstructor
  @JsonNaming(SnakeCaseStrategy.class)
  public static class EndpointDto {

    private String id;
    private String label;
    private String serialNumber;
    private String systemVersion;
    private String features;
    private Long memoryTotal;
    private Long storageTotal;
    private Long startupTimestamp;
    private Long initTimestamp;
    private String status;
    private String clientCert;
    private String clientKey;
    private String ttnLanIp;
    private String ttnNetmask;
    private Integer port;
    private String gateway;
    private String ipv6Gateway;
    private String ip;
    private String ipv6;
    private Integer ipv4netmask;
    private Integer ipv6netmask;
    private Long mtime;

    private String latitude;
    private String longitude;
    private String desc;

    private Integer fecPort;
    private Boolean enable;
    private String level;

    @org.jetbrains.annotations.NotNull
    public String getIpv4Cidr() {
      return getGateway() + "/" + getIpv4netmask();
    }
  }

  @Data
  @NoArgsConstructor
  @JsonNaming(SnakeCaseStrategy.class)
  public static class WhiteList {

    private String id;
    private String subnet;
  }

  @Data
  @NoArgsConstructor
  @JsonNaming(SnakeCaseStrategy.class)
  public static class TtnConfig {

    @NotEmpty
    private String id;

    @NotEmpty
    private String key;

    @NotEmpty
    private String value;

    @NotNull
    private Long mtime;
  }

  @Getter
  public static class EndpointMergeEvent extends ApplicationEvent {

    private final List<EndpointDto> endpointDtoList;

    public EndpointMergeEvent(Object source, List<EndpointDto> endpointDtoList) {
      super(source);
      this.endpointDtoList = endpointDtoList;
    }
  }

  @Getter
  public static class TtnConfigFullDumpEvent extends ApplicationEvent {

    private final List<TtnConfig> ttnConfigList;

    public TtnConfigFullDumpEvent(
        Object source, List<TtnConfig> ttnConfigList) {
      super(source);
      this.ttnConfigList = ttnConfigList;
    }
  }
}
