package com.chaowen.springboottemplate.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.chaowen.springboottemplate.base.common.Utils;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Token {

  private static long exp;
  private static JWTSigner SINGER;
  private Map<String, Object> body;
  private String token;

  public Token(@NotNull String token) {
    this.token = token;
    Utils.trycatch(() -> {
      this.body = JWTUtil.parseToken(token).getPayloads();
    }).throwIfEx();

    if (!formatValid()) {
      throw new RuntimeException("invalid token format");
    }
  }

  public static void init(String signkey, long exp) {

    Token.exp = exp;
    Token.SINGER = JWTSignerUtil.hs512(signkey.getBytes());
  }

  public static void main(String[] args) throws InterruptedException {

    Token.init("111111", 1000 * 60);
    // Test factory method
    System.out.println("Creating a new token...");
    Token newToken = Token.create("nobel");
    System.out.println("Generated Token: " + newToken);

    // Test get method
    System.out.println("Subject: " + newToken.get("sub"));
    System.out.println("Issuer: " + newToken.get("iss"));

    // Test if token is valid
    System.out.println("Is token expired? " + newToken.expired());
    System.out.println("Is token signature valid? " + newToken.verifySig());

    //Thread.sleep(2000);

    // Test refreshExpire
    System.out.println("Refreshing expiration...");
    newToken.refreshExpire();
    System.out.println("Token after refreshing expiration: " + newToken);

    // Wait to test expiration
    try {
      System.out.println("Waiting for token to expire...");
      Thread.sleep(1000); // Wait 1 second for demonstration (adjust as needed)
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Check token validity again
    System.out.println("Is token expired after wait? " + newToken.expired());
  }

  // factory method to create a token
  public static Token create(@NotNull String subject) {
    return create(subject, exp);
  }

  public static Token create(
      @NotNull String subject, @NotNull Long durationMs,
      @NotNull Map<String, String> customPayload, @NotNull KeyPair keyPair) {
    Map<String, Object> payload = constructPayload(subject, durationMs, customPayload);
    var token = JWT.create().addPayloads(payload)
        .sign(JWTSignerUtil.createSigner("SM3withSm2", keyPair));
    return new Token(token);
  }

  public static Token create(
      @NotNull String subject, @NotNull Long durationMs) {
    Map<String, Object> payload = constructPayload(subject, durationMs,
        new HashMap<>());
    var token = JWT.create().addPayloads(payload).sign(SINGER);
    return new Token(token);
  }

  private static Map<String, Object> constructPayload(
      @NotNull String subject, @NotNull Long durationMs,
      @NotNull Map<String, String> customPayload) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("sub", subject); // subject
    payload.put("iss", TOKEN_ISSUER); // issuer
    payload.put("iat", System.currentTimeMillis() / 1000); // issued at
    payload.put("exp", (System.currentTimeMillis() / 1000) +
                       (durationMs / 1000)); // expiration time
    payload.putAll(customPayload);
    return payload;
  }

  public static Token createExpiredTokenForTest(@NotNull String subject) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("sub", subject); // subject
    payload.put("iss", TOKEN_ISSUER); // issuer
    payload.put("iat", System.currentTimeMillis() / 1000 - 10); // issued at
    payload.put("exp",
        System.currentTimeMillis() / 1000 - 5); // expiration time

    String token = JWT.create().addPayloads(payload).sign(SINGER);
    return new Token(token);
  }

  @NotNull
  public static String markTokenWithUkey(
      @NotNull String token, @NotNull String ukeyUuid) {
    var t = new Token(token);
    t.set(TOKEN_PAYLOAD_UKEY_UUID_ATTR, ukeyUuid);
    return t.toString();
  }

  @Nullable
  public String get(@NotNull String key) {
    return body != null ? (String) body.get(key) : null;
  }

  public String set(@NotNull String key, @NotNull String value) {
    if (body != null) {
      body.put(key, value);
      build();
    }
    return token;
  }

  @NotNull
  private String build() {
    token = JWT.create().addPayloads(body).sign(SINGER);
    return token;
  }

  @Nullable
  public Long getExp() {
    return body != null && body.containsKey("exp") ? ((Number) body.get(
        "exp")).longValue() : null;
  }

  @NotNull
  @Override
  public String toString() {
    return token;
  }

  public boolean expired() {
    if (body != null && body.containsKey("exp")) {
      long exp = ((Number) body.get("exp")).longValue();
      return exp < (new Date().getTime() / 1000);
    }
    return false;
  }

  boolean formatValid() {
    try {
      // check if all required payload keys exist
      return body.containsKey("sub") && body.containsKey("iss") &&
             body.containsKey("iat") && body.containsKey("exp");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean verifySig() {
    return JWTUtil.verify(token, SINGER);
  }

  public boolean verifySig(@NotNull KeyPair keyPair) {
    return JWTUtil.verify(token,
        JWTSignerUtil.createSigner("SM3withSm2", keyPair));
  }

  // Refresh the expiration time
  public void refreshExpire() {
    if (body != null) {
      body.put("exp", (System.currentTimeMillis() / 1000) + (exp / 1000));
      build();
    }
  }

  // CONST
  public static final String TOKEN_ISSUER = "com.chaowen";
  public static final String TOKEN_FIELD_TYPE = "type";
  public static final String TOKEN_FIELD_SUB = "sub";
  public static final String TOKEN_PAYLOAD_UKEY_UUID_ATTR = "ukey-uuid";
}
