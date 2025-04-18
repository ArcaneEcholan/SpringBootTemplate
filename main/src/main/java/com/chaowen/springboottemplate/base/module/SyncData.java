package com.chaowen.springboottemplate.base.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class SyncData {

  @JsonProperty("fuse_pki")
  private List<Map> fusePki;

  private List<Map> endpoint;

  private List<Map> whitelist;

  @JsonProperty("ttn_config")
  private List<Map> ttnConfig;
}
