package com.chaowen.springboottemplate.base.auxiliry;

import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.regex.Pattern;

public class MaskingConverter extends CompositeConverter<Object> {

  // match JWT tokens
  // eyJ is the first few letters of the encoded jwt
  private static final Pattern TOKEN_PATTERN =
      Pattern.compile("eyJ[\\w-]+\\.[\\w-]+\\.[\\w-]+");

  // match PEM certificate content
  private static final Pattern PEM_CERT_PATTERN = Pattern.compile(
      "-----BEGIN CERTIFICATE-----[\\s\\S]+?-----END CERTIFICATE-----");

  // match PEM private key content
  private static final Pattern PEM_PRIVATE_KEY_PATTERN = Pattern.compile(
      "-----BEGIN PRIVATE KEY-----[\\s\\S]+?-----END PRIVATE KEY-----");

  @Override
  protected String transform(Object event, String in) {
    // mask JWT tokens
    String maskedMessage =
        TOKEN_PATTERN.matcher(in).replaceAll("****TOKEN****");

    // mask PEM certificate content
    maskedMessage = PEM_CERT_PATTERN.matcher(maskedMessage)
        .replaceAll("****CERTIFICATE****");

    // mask PEM private key content
    maskedMessage = PEM_PRIVATE_KEY_PATTERN.matcher(maskedMessage)
        .replaceAll("****PRIVATE_KEY****");

    return maskedMessage;
  }
}
