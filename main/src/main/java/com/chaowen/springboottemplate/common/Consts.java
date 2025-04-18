package com.chaowen.springboottemplate.common;

public class Consts {

  // 10 * 1000
  public static final Long DEFAULT_MONITOR_DATA_FETCHING_INTERVAL = 10000L;

  // 30 * 86400 * 1000
  public static final Long DEFAULT_MONITOR_DATA_KEEPING_INTERVAL = 2592000000L;

  public static final String EMPTY_STRING = "";

  public static final String APP_ACCESS_TOKEN = "app-access-token";

  public static final String SUPER_ADMIN_USERNAME = "super";
  public static final String SYSTEM_ADMIN_USERNAME = "admin";
  public static final String NORMAL_USER_USERNAME = "user";

  public static final String SUPER_ADMIN_ROLE = "super-admin";
  public static final String SUPER_ADMIN_ROLE_DESC = "超级管理员";

  public static final String DEFAULT_ROLE = "default";
  public static final String DEFAULT_ROLE_DESC = "默认角色";

  public static final String LOGIN_API = "/access/login";
  public static final String INIT_STATUS = "init_status";
  public static final String SUPER_ADMIN_STATUS = "super_admin_status";
  public static final String SYSTEM_ADMIN_STATUS = "system_admin_status";
  public static String REQUEST_URI_KEY = "REQUEST_URI";
}
