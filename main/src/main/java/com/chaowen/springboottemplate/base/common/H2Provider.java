package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.auxiliry.DbProviders;
import javax.sql.DataSource;
import lombok.var;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.stereotype.Component;

@Component
public class H2Provider implements DbProviders.DbProvider {

  @Override
  public DataSource dataSource() {
    var dataSource = new JdbcDataSource();
    dataSource.setURL(
        "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;");
    dataSource.setUser("sa");
    dataSource.setPassword("password");
    return dataSource;
  }
}
