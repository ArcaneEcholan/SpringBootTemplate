package com.chaowen.springboottemplate.common;

import com.chaowen.springboottemplate.base.common.AppResponses.RespCode;
import lombok.Getter;

@Getter
public enum CenterRespCode implements RespCode {
  @Deprecated SUCCESS("成功"),
  @Deprecated SERVER_ERROR("服务内部错误"),
  @Deprecated FRONT_END_PARAMS_ERROR("参数错误"),

  // auth
  USERNAME_PASSWORD_AUTH_FAILED("用户名或密码错误"),
  TOKEN_EXPIRED("令牌已过期"),
  TOKEN_MISSING("令牌缺失"),
  TOKEN_INVALID("令牌无效"),
  PERMISSION_NOT_ADEQUATE("用户权限不足"),

  // user & role & permission
  CAN_NOT_GRANT_SUPER_ADMIN_TO_USER("不能将超级管理员添加给用户"),
  ROLE_NOTFOUND("角色不存在"),
  PERMISSION_NOT_FOUND("权限不存在"),
  CAN_NOT_DELETE_SELF("用户不能删除自身"),
  CHANGE_PASSWORD_OLD_PASSWORD_MISMATCH("旧密码验证失败"),
  USER_NOTFOUND("用户不存在"),

  // ukey
  BIND_UKEY_TARGET_USER_NOT_FOUND("待绑定 UKEY 的用户不存在"),
  USER_NEED_TO_BIND_UKEY("用户需要绑定 UKEY"),
  TOKEN_UKEY_INFO_CHECK_FAILED("用户令牌信息与 UKEY 不匹配"),
  USER_NOT_BOUND_WITH_ANY_UKEY("用户没有绑定任何 UKEY"),
  CALL_UKEY_FAILED("UKEY 客户端错误"),
  SIG_VERIFY_FAILED("签名校验失败"),
  ASYNC_DECRYPT_FAILED("非对称解密失败"),
  UKEY_USER_NUMBER_NOT_ENOUGH("持有 UKEY 的管理员人数不足，无法解绑 UKEY"),
  USER_ALREADY_BOUND_WITH_OTHER_UKEY("用户已经绑定了 UKEY"),
  UKEY_ALREADY_BOUND_TO_OTHER_USER("UKEY 已被绑定到其他用户"),
  CALL_EP_FAILED("本地调用终端失败"),
  UKEY_VERIFY_SESSION_MISMATCH("UKEY 验证 session 不匹配"),

  // network related
  NIC_SHOULD_BE_CONSISTENT_WITH_LOCAL("网卡在本地不存在"),
  BOND_MODE_ERROR("bond 模式错误"),
  BOND_NAME_NOT_EXISTS_IN_DEVS("bond 设备名称在设备列表必须存在"),
  BOND_NAME_DUPLICATE_WITH_NIC_NAME("bond 设备名称不能跟 nic 设备名称一样"),
  SLAVE_DEV_NAME_IS_EXISTS_IN_DEVS(
      "bond 设备中 slave 的 nic 设备名称不能出现在 dev 中"),
  UNKNOWN_SLAVES_NAME("未知的 nic 设备名称在 slave 中"),
  ROLE_DEV_NAME_IS_NOT_EXISTS("未知的设备名称在角色配置中"),
  ROLE_DEV_NAME_HAS_BEEN_REMOVED("已经移除的设备名称在角色配置中"),
  NETPORT_NOTFOUND("网口不存在"),
  SET_GATEWAY_FAILURE("网关ip与网卡ip不在同一网段 "),
  BRIDGE_NAME_REPEAT("网桥名称重复"),
  BRIDGE_JOIN_FAILED("接入网桥失败"),
  BRIDGE_CREATE_FAILED("创建网桥失败"),
  BRIDGE_IS_CONNECTED_NIC("网桥已经接入了别的网卡"),
  BRIDGE_DELETE_FAILED("网桥删除失败"),

  //backup
  ACCESS_TO_PATH_FORBIDDEN("无权访问路径"),
  FILE_NOT_FOUND("文件不存在"),
  FULL_INTERVAL_ERROR("以毫秒为单位，需要为小时的倍数"),
  INC_INTERVAL_ERROR("以毫秒为单位，需要为小时的倍数"),
  NUMBER_HAS_REACHED_LIMIT("目前最多只支持添加一个备份任务"),

  // system init
  CENTER_ALREADY_INIT("中心已经初始化"),
  SUPER_ADMIN_NOT_INIT("超级管理员没有初始化"),
  SYSTEM_ADMIN_NOT_INIT("系统管理员没有初始化"),

  // email
  EMAIL_SENDER_NOT_FOUND("邮件发送者不存在"),
  EMAIL_SENDER_TEST_FAILED("邮件发送者配置测试不通过"),
  EMAIL_DEFAULT_SENDER_NOT_SET("邮件默认发送者没有设置"),
  EMAIL_ACCOUNT_OR_TOKEN_OR_SMTPADDR_ERROR(
      "邮件发送者账号或密码或smtp地址错误"),
  EMAIL_SERVER_CONNECT_TIMEOUT("邮件服务连接超时"),

  // other
  RESTART_SYSLOG_SERVICE_FAILED("重启系统日志服务失败"),
  GET_SYSLOG_FAILED("获取系统日志失败"),
  BASIC_PLATFORM_CERTS_NOT_CONFIGURED("证书没有配置"),
  CERT_FORMAT_ERROR("证书格式错误"),

  /* no use */

  // user and role
  USER_OR_ROLE_DOES_NOT_EXIST(""),

  // no use for now
  FILE_NAME_DUPLICATE(""),
  ROLE_NAME_DUPLICATE("同名角色已存在"),
  USER_ALREADY_HAS_ROLE("重复将角色赋予给用户"),
  ROLE_ALREADY_HAS_PERMISSION("重复将权限赋予给角色"),
  CAN_NOT_MODIFY_PRESET_ROLE("不能修改预置角色"),
  CAN_NOT_DELETE_SUPER_ADMIN_USER(""),
  IP_FORMAT_ERROR(""),
  // file & artifact
  FILE_CREATE_ERROR(""),
  FILE_VERSION_EXISTS("文件版本已存在"),
  ARTIFACT_EXISTS(""),
  FILE_NOT_EXISTS("文件不存在"),
  FILE_TYPE_ERROR(""),
  ART_NOT_EXISTS(""),
  DIR_VERSION_NOT_SUPPORT(""),
  FILE_VERSION_NOT_FOUND(""),
  PERMISSION_NOTFOUND("权限不存在"),
  USERNAME_INVALID("用户名不符合规范"),
  USERNAME_DUPLICATE("同名用户已存在"),
  USER_DISABLED("用户被禁用"),
  FILE_INVALID("文件失效"),
  FILENAME_DUPLICATE("文件已存在"),
  FILE_NOTFOUND("文件不存在"),
  CAN_NOT_DELETE_PRESET_USER("不能删除预置用户"),
  CAN_NOT_MODIFY_SELF("不能修改自己"),
  CAN_NOT_MODIFY_PRESET_USER("不能修改预置用户"),
  NOT_SUPPORT_MODE_NOT_SUPPORTED("API 模式不支持"),
  YOUR_PERMISSION_LOWER_THAN_TARGET("您的权限低于操作对象"),
  ENTITY_ID_CANNOT_BE_SAME("来访者ID和操作者对象相同"),
  CANNOT_GRANT_PERMISSION_TO_USER(
      "您没有相应权限，故不能将其授予他人或从他人撤销"),
  CAN_NOT_MODIFY_SUPER_ADMIN("不能修改超级管理员配置"),
  UNEXPECTED_WS_TYPE("WebSocket 数据包类型错误"),
  CLUSTER_NODE_BIND_REQ_FAILED("集群节点绑定请求失败"),
  CLUSTER_NODE_UID_DUPLICATE("集群节点 UID 与本机冲突"),
  CLUSTER_INFO_NOT_CONFIGURED("集群信息未配置"),
  CLUSTER_NODE_NOTFOUND("集群节点不存在"),
  CLUSTER_NODE_UNREGISTER("节点未绑定到此集群"),
  CLUSTER_NODE_INFO_INVALID("节点信息有误"),
  CLUSTER_NODE_VERIFY_FAILED("节点 TOKEN 公钥验证不通过"),
  KEY_FORMAT_ERROR("密钥格式错误"),
  CLUSTER_NODE_TOKEN_AUTH_FAILED("集群节点 TOKEN 认证失败"),
  CLUSTER_ONLY_STANDALONE_CAN_BIND_PRIMARY(
      "只有单机节点可以向主节点发起绑定请求"),
  CLUSTER_ONLY_SECONDARY_CAN_LEAVE_CLUSTER("只有从节点可以离开集群"),
  CLUSTER_ONLY_SUPPORT_CREATE_CLUSTER_FROM_STANDALONE(
      "只支持从单机节点创建集群"),
  CLUSTER_ONLY_SUPPORT_EVICT_NODE_FROM_PRIMARY("只支持从主节点踢除其他节点"),
  CLUSTER_ONLY_SUPPORT_DESTROY_CLUSTER_FROM_PRIMARY("只支持从主节点销毁集群"),
  SET_NETDEV_UP_FAILED("启动网络设备失败"),
  BACKUP_NOTFOUND("备份记录不存在"),
  API_NOT_AVAILABLE("API 不可用"),
  BACKUP_CHAIN_VALIDITY_CHECK_FAILED("备份链条完整性检查失败");

  private final String msg;

  CenterRespCode(String msg) {
    this.msg = msg;
  }

  public String getCode() {
    return name();
  }

}
