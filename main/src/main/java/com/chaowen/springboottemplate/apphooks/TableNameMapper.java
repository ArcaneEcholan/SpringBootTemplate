package com.chaowen.springboottemplate.apphooks;

import com.chaowen.springboottemplate.base.common.Utils;

public class TableNameMapper {

  private final String prefix;
  private final String suffix;

  public TableNameMapper(String prefix, String suffix) {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  /**
   * Customize the table name here, it'll be applied when creating tables and
   * performing mybatis operations
   */
  public String map(String originTableName) {
    // remove "`" if any, for example: `user` => user
    originTableName = originTableName.replace("`", "");
    // final tablename = ${prefix} + ${originTableName} + ${suffix}
    return Utils.fmt("`{}{}{}`", prefix, originTableName, suffix);
  }
}


