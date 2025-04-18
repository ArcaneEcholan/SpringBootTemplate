package com.chaowen.springboottemplate.base.module;

import lombok.Getter;

@Getter
public enum OpLogTypes {

  EMAIL_SENDER_TEST(""),
  EMAIL_RECEIVER_TEST(""),
  EMAIL_UPDATE_SENDER(""),
  EMAIL_DELETE_SENDER(""),
  EMAIL_CREATE_SENDER("create_sender"),
  HANDLE_WEBSOCKET("hand_websocket"),
  INIT_ENDPOINT("init_endpoint"),
  GENERATE_ENDPOINT_TTN_CERT("generate_endpoint_ttn_cert"),
  REVOKE_ENDPOINT_TTN_CERT("revoke_endpoint_ttn_cert"),
  UNBIND_ENDPOINT("unbind_endpoint"),
  UPDATE_SYSTEM_MONITOR("update_system_monitor"),
  UPDATE_SYSTEM_DNS("update_system_dns"),
  UPDATE_SYSTEM_NETWORK("UPDATE_SYSTEM_NETWORK"),
  UPDATE_SYSTEM_NETPORTS("update_system_netports"),
  UPDATE_SYSTEM_ACCESS("update_system_access"),
  UPDATE_SYSTEM_PORT("update_system_port"),
  UPDATE_TTN_CONFIG("update_ttn_config"),
  UPDATE_SYSTEM_SERVICE("update_system_service"),
  SYSTEM_INIT("system_init"),
  BIND_UKEY("bind_ukey"),
  UNBIND_UKEY("unbind_ukey"),
  VERIFY_UKEY("verify_ukey"),
  CHANGE_PASSWORD("change_password"),
  LOGIN("login"),
  UPDATE_EVENT_LOGROTATE("update_operation_logrotate"),
  UPDATE_NTP_CONNECT_INFO("update_ntp_connect_info"),
  UPDATE_OPERATION_LOGROTATE("update_operation_logrotate"),
  UPDATE_SYSLOG_CONFIG("update_syslog_config");

  private final String value;

  OpLogTypes(String value) {
    this.value = value;
  }

}
