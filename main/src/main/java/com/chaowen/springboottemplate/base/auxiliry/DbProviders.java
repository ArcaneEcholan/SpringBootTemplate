package com.chaowen.springboottemplate.base.auxiliry;

import javax.sql.DataSource;

public class DbProviders {

  public static interface DbProvider {

    DataSource dataSource();
  }

}
