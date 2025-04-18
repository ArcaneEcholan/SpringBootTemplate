package com.chaowen.springboottemplate.apphooks;

import com.chaowen.springboottemplate.base.common.Utils;

import static com.chaowen.springboottemplate.apphooks.MachineIdGetter.getMachineId;

import com.chaowen.springboottemplate.base.BeforeBeanInitializer.BeforeBeanInitHook;
import com.chaowen.springboottemplate.base.BeforeBeanInitializer.BeforeBeanInitOrder;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.var;
import org.springframework.core.env.Environment;

public class BeforeBeanInitHooks {

  /**
   * Control the order of hooks here
   */
  public static class Order implements BeforeBeanInitOrder {

    @Override
    public void getOrderedHooks(List<BeforeBeanInitHook> hooks) {
      // create your custom hooks
      var tableSuffixInit = new TableSuffixInitHook();

      // add your own hooks here, control the order by the list
      hooks.add(tableSuffixInit);
    }
  }

  public static class TableSuffixInitHook implements BeforeBeanInitHook {

    public static final String TABLE_NAME_MAPPER = "TABLE_NAME_MAPPER";

    @Override
    public void run(Environment env, Map<String, Object> customPropsMap) {

      // assign "" if you want totally normal table name
      var prefix = "";
      var suffix = Utils.fmt("_{}", getMachineId());
      customPropsMap.put(TABLE_NAME_MAPPER,
          new TableNameMapper(prefix, suffix));
    }
  }

}

class MachineIdGetter {

  public static String getMachineId() {
    String id = tryGetLinuxMachineId();
    if (id != null) {
      System.out.println("linux machine id: " + id);
      return id;
    }
    id = tryGetMacAddress();
    if (id != null) {
      System.out.println("max address id: " + id);
      return id;
    }

    return generateRandomMachineId();
  }

  private static String tryGetLinuxMachineId() {
    try {
      if (Files.exists(Paths.get("/etc/machine-id"))) {
        String id =
            new String(Files.readAllBytes(Paths.get("/etc/machine-id"))).trim();
        if (!id.isEmpty()) {
          return id;
        }
      }
    } catch (IOException ignored) {
    }
    return null;
  }

  private static String tryGetMacAddress() {
    try {
      for (NetworkInterface ni : Collections.list(
          NetworkInterface.getNetworkInterfaces())) {
        if (ni == null || ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
          continue;
        }
        byte[] mac = ni.getHardwareAddress();
        if (mac != null && mac.length > 0) {
          StringBuilder sb = new StringBuilder();
          for (byte b : mac) {
            sb.append(String.format("%02X", b));
          }
          return sb.toString();
        }
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  private static String generateRandomMachineId() {
    var uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
    System.out.println("use uuid: " + uuid);
    return uuid;
  }

  public static void main(String[] args) {
    System.out.println(getMachineId());
  }
}
