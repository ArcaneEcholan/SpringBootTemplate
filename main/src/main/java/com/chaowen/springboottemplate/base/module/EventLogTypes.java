package com.chaowen.springboottemplate.base.module;

import lombok.Getter;

@Getter
public enum EventLogTypes {

  NTP_SYNC("ntp_sync"),
  LDAP_SYNC_RESULT("ldap_sync_result"),

  ENDPOINT_OFFLINE("endpoint_offline"),
  ENDPOINT_ONLINE("endpoint_online"),
  ENDPOINT_STORAGE_ALARM("endpoint_storage_alarm"),
  ENDPOINT_MEMORY_ALARM("endpoint_memory_alarm"),
  ENDPOINT_CPU_ALARM("endpoint_cpu_alarm"),
  ENDPOINT_TRAFFIC_ALARM("endpoint_traffic_alarm"),

  EMAIL_SEND("email_send"),
  CENTER_STORAGE_ALARM("center_storage_alarm"),
  CENTER_MEMORY_ALARM("center_memory_alarm"),
  CENTER_CPU_ALARM("center_cpu_alarm"),
  CENTER_TRAFFIC_ALARM("center_traffic_alarm"),
  NOTIFY_ENDPOINT("notify_endpoint"),
  FEC_RESTART("fec_restart"),
  TTN_RESTART("ttn_restart"),
  FEC_HOT_RELOAD("fec_hot_reload"),
  UNBIND_ENDPOINT("UNBIND_ENDPOINT"),
  ROUTE_UPDATE("ROUTE_UPDATE"),
  ASSIGN_VIRTUAL_IP("ASSIGN_VIRTUAL_IP"),
  CLEAN_CENTER_MONITOR_DATA("CLEAN_CENTER_MONITOR_DATA"),
  SERVICE_RESTART("SERVICE_RESTART")
  ;

  private final String value;

  EventLogTypes(String value) {
    this.value = value;
  }

}
