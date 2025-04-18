package com.chaowen.springboottemplate.common;

import com.chaowen.springboottemplate.base.common.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppProperties {

  @Autowired
  Environment environment;

  public String getFuseRoot() {
    return environment.getProperty("fuse.root");
  }

  public String getDbDockerComposeFile() {
    return com.chaowen.springboottemplate.base.common.Utils.fmt("{}/db/docker-compose.yml", getDockerRootDir());
  }

  public String getAppRootDir() {
    return appRootDir;
  }

  public String getAppRunDir() {
    return appRunDir;
  }

  public String getBackupMountDir() {
    return com.chaowen.springboottemplate.base.common.Utils.fmt("{}/db/mysql_backups", getDockerRootDir());
  }

  public String getBackupMountDirInner() {
    return com.chaowen.springboottemplate.base.common.Utils.fmt("/files");
  }

  public String getBackupJobPath() {
    return com.chaowen.springboottemplate.base.common.Utils.fmt("/etc/backup_job.json", getAppRunDir());
  }

  public String getDbDataDir() {
    return Utils.fmt("{}/db/mysql_data", getDockerRootDir());
  }

  public String getMariadbContainerName() {
    return environment.getProperty("mariadb_container_name");
  }

  public String getUnixsockPath() {
    return unixsock;
  }

  @Value("${unixsock.path:/tmp/app.socket}")
  private String unixsock;

  @Value("${APP_ROOT_DIR:/etc/smd/files/bin}")
  private String appRootDir;

  @Value("${APP_RUN_DIR:/tmp/.ttn}")
  private String appRunDir;

  @Getter
  @Value("${DOCKER_ROOT_DIR:/etc/smd/files/docker}")
  private String dockerRootDir;

  @Getter
  @Value("${backup.path}")
  private String backupPath;

  @Getter
  @Value("${cluster.data-sync:60000}")
  private Long clusterDataSyncInterval;
}
