package com.chaowen.springboottemplate.base.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class IpTool {

  static byte[] calculateNetworkAddress(
      InetAddress ipAddress, int prefixLength) {
    byte[] addressBytes = ipAddress.getAddress();
    byte[] maskBytes = new byte[addressBytes.length];

    int fullBytes = prefixLength / 8;
    int remainingBits = prefixLength % 8;

    // Fill maskBytes with 1s according to prefix length
    Arrays.fill(maskBytes, 0, fullBytes, (byte) 0xFF);
    if (remainingBits > 0) {
      maskBytes[fullBytes] = (byte) (0xFF << (8 - remainingBits));
    }

    // Apply mask to the address to get the network address
    byte[] networkBytes = new byte[addressBytes.length];
    for (int i = 0; i < addressBytes.length; i++) {
      networkBytes[i] = (byte) (addressBytes[i] & maskBytes[i]);
    }
    return networkBytes;
  }

  /**
   * 1.1.1.1/11 -> networkAddress=[1, 0, 0, 0], prefixLength=11
   *
   * <p>172.31.255.253/30 -> networkAddress=[-84(byte), 31(byte), -1(byte),
   * -4(byte)], prefixLength=11
   */
  @SneakyThrows
  @NotNull
  public static NetworkAddress parseCidr(String cidr) {
    var parts = cidr.split("/");
    InetAddress ipAddress = null;
    {
      String[] octets = parts[0].split("\\.");
      byte[] ipBytes = new byte[4];
      for (int i = 0; i < 4; i++) {
        ipBytes[i] = (byte) Integer.parseInt(octets[i]);
      }
      ipAddress = InetAddress.getByAddress(ipBytes);
    }

    int prefixLength = Integer.parseInt(parts[1]);

    byte[] ip = calculateNetworkAddress(ipAddress, prefixLength);
    return new NetworkAddress(ipAddress.getHostAddress(), ip, prefixLength);
  }

  /**
   * 24 => 255.255.255.0
   *
   * <p>18 => 255.255.192.0
   */
  static String prefixLenToNetmask(int prefixLength) {
    int subnetMask = (0xFFFFFFFF << (32 - prefixLength));
    return ((subnetMask >> 24) & 0xFF) + "." + ((subnetMask >> 16) & 0xFF) +
           "." + ((subnetMask >> 8) & 0xFF) + "." + (subnetMask & 0xFF);

  }

  // convert IPv4 netmask to prefix length
  public static int netmaskToPrefixLen(String netmask) {
    // validate netmask format
    try {
      InetAddress inetAddress = InetAddress.getByName(netmask);
      byte[] bytes = inetAddress.getAddress();

      // count the number of 1 bits in the netmask
      int prefixLength = 0;
      for (byte b : bytes) {
        prefixLength += Integer.bitCount(b & 0xFF);
      }
      return prefixLength;
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException(
          "Invalid IPv4 netmask format: " + netmask, e);
    }
  }

  public static String getSubnetMaskFromIpCount(int ipCount) {
    if (ipCount < 1 || ipCount > 16777214) {
      throw new IllegalArgumentException(
          "IP count must be between 1 and 16777214.");
    }
    var nextPower =
        (int) Math.pow(2, Math.ceil(Math.log(ipCount) / Math.log(2)));
    // 计算 CIDR 前缀长度，要求是向上取整，确保 IP 数量包含所有地址
    var prefixLength = 32 - (int) Math.ceil(Math.log(nextPower) / Math.log(2));

    return prefixLenToNetmask(prefixLength);
  }

  public long ipToLong(String ipAddress) {
    String[] addrArray = ipAddress.split("\\.");
    long num = 0;
    for (int i = 0; i < addrArray.length; i++) {
      int power = 3 - i;
      num += (long) ((Integer.parseInt(addrArray[i]) % 256) *
                     Math.pow(256, power));
    }
    return num;
  }

  public String longToIp(long ip) {
    return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." +
           ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
  }

  @ToString
  public static class NetworkAddress {

    @Getter
    @NotNull
    final String ipPart;

    @Getter
    final byte @NotNull [] networkAddress;

    @Getter
    final int prefixLength;

    NetworkAddress(
        @NotNull String ipPart, byte @NotNull [] networkAddress,
        int prefixLength) {
      this.ipPart = ipPart;
      this.networkAddress = networkAddress;
      this.prefixLength = prefixLength;
    }

    /**
     * "172.31.255.253/30" -> 172.31.255.252
     */
    @SneakyThrows
    @NotNull
    public String getNetworkAddrStr() {
      return InetAddress.getByAddress(networkAddress).getHostAddress();
    }

    @SneakyThrows
    @NotNull
    public String getNetworkAddrCidr() {
      return Utils.fmt("{}/{}", getNetworkAddrStr(), prefixLength);
    }

    @NotNull
    public String getNetmask() {
      return IpTool.prefixLenToNetmask(prefixLength);
    }
  }

}
