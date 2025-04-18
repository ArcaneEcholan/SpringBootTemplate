package com.chaowen.springboottemplate.base.common;

import cn.hutool.core.date.DateTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.var;

public class DateTimeUtils {

  public static String nowString() {
    var now = DateTime.now();
    return now.toString("yyyy-MM-dd HH:mm:ss");
  }

  public static String format(Date date) {
    var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return formatter.format(date);
  }

}
