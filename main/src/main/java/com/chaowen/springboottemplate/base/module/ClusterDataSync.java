package com.chaowen.springboottemplate.base.module;

import java.util.Map;

public class ClusterDataSync {
  public static interface DataSyncModule {

    Map<String, Object> dataSyncGetData();

    void dataSyncRecvData(String data);

  }

}
