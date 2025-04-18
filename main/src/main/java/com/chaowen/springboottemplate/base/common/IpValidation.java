package com.chaowen.springboottemplate.base.common;

import static com.chaowen.springboottemplate.base.common.IpValidation.IpType.V4;
import static com.chaowen.springboottemplate.base.common.IpValidation.IpType.V46;
import static com.chaowen.springboottemplate.base.common.IpValidation.IpType.V6;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isCidr;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isIp;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isIpv4;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isIpv4Cidr;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isIpv6;
import static com.chaowen.springboottemplate.base.common.Utils.IpUtils.isIpv6Cidr;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.chaowen.springboottemplate.base.auxiliry.Staticed;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

public class IpValidation {

  public enum IpType {
    V4,
    V6,
    V46
  }

  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  @Constraint(validatedBy = CidrValidator.class)
  public @interface ValidCidrObj {

    String message() default "not valid cidr";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  @Constraint(validatedBy = CidrValidatorStr.class)
  public @interface ValidCidrStr {

    String message() default "not valid cidr";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    IpType type() default V46;
  }

  @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  @Constraint(validatedBy = IpValidator.class)
  public @interface ValidIpStr {

    IpType type() default V46;

    String message() default "not valid ip";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

  @Data
  @NoArgsConstructor
  @JsonNaming(SnakeCaseStrategy.class)
  public static class Ip {

    @NotNull
    private Boolean isIpv4;

    @NotEmpty
    private String cidr;
  }

  @Component
  @Staticed
  public static class CidrValidator
      implements ConstraintValidator<ValidCidrObj, Ip> {

    @Override
    public boolean isValid(Ip ip, ConstraintValidatorContext context) {
      if (ip == null || ip.getCidr() == null || ip.getIsIpv4() == null) {
        return true;
      }

      boolean isValid =
          ip.getIsIpv4() ? isIpv4Cidr(ip.getCidr()) : isIpv6Cidr(ip.getCidr());
      if (!isValid) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("not valid cidr")
            .addConstraintViolation();
      }
      return isValid;
    }
  }

  @Component
  @Staticed
  public static class CidrValidatorStr
      implements ConstraintValidator<ValidCidrStr, String> {

    ValidCidrStr anno;

    @Override
    public void initialize(ValidCidrStr constraintAnnotation) {
      this.anno = constraintAnnotation;
    }

    @Override
    public boolean isValid(String cidr, ConstraintValidatorContext context) {
      if (cidr == null) {
        return true;
      }

      if (anno.type() == V4) {
        boolean isValid = isIpv4Cidr(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid cidr")
              .addConstraintViolation();
        }
        return isValid;
      } else if (anno.type() == V6) {
        boolean isValid = isIpv6Cidr(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid cidr")
              .addConstraintViolation();
        }
        return isValid;
      } else if (anno.type() == V46) {
        boolean isValid = isCidr(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid cidr")
              .addConstraintViolation();
        }
        return isValid;
      }

      throw new RuntimeException("unsupported cidr validation");
    }
  }

  @Component
  @Staticed
  public static class IpValidator
      implements ConstraintValidator<ValidIpStr, String> {

    ValidIpStr anno;

    @Override
    public void initialize(ValidIpStr constraintAnnotation) {
      this.anno = constraintAnnotation;
    }

    @Override
    public boolean isValid(String cidr, ConstraintValidatorContext context) {
      if (cidr == null) {
        return true;
      }

      if (anno.type() == V4) {
        boolean isValid = isIpv4(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid ipv4")
              .addConstraintViolation();
        }
        return isValid;
      } else if (anno.type() == V6) {
        boolean isValid = isIpv6(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid ipv6")
              .addConstraintViolation();
        }
        return isValid;
      } else if (anno.type() == V46) {
        boolean isValid = isIp(cidr);
        if (!isValid) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("not valid ip")
              .addConstraintViolation();
        }
        return isValid;
      }

      throw new RuntimeException("unsupported ip validation");
    }
  }
}
