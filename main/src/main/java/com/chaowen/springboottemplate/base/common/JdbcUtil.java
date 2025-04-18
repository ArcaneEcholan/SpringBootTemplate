package com.chaowen.springboottemplate.base.common;

import java.sql.Connection;
import java.sql.DriverManager;

import com.chaowen.springboottemplate.base.auxiliry.*;
import org.jetbrains.annotations.NotNull;

public class JdbcUtil {

  @NotNull
  public static Connection getConnection(
      String host, int port, String username, String passwd, String dbName)
      throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(DbProperties.formUrl(host, port, dbName), username,
        passwd);
  }
}
