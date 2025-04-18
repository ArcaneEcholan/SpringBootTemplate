package com.chaowen.springboottemplate.base.testsbase;

import com.chaowen.springboottemplate.base.common.Consts;
import com.chaowen.springboottemplate.base.common.IpValidation;
import com.chaowen.springboottemplate.base.common.JsonValidationUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
class Entity {

  @IpValidation.ValidCidrStr String cidr;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Entity1 {

  @Valid @IpValidation.ValidCidrObj IpValidation.Ip ipObj;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Entity2 {

  @IpValidation.ValidIpStr String ip;
  @IpValidation.ValidIpStr(type = IpValidation.IpType.V4) String ipv4;
  @IpValidation.ValidIpStr(type = IpValidation.IpType.V6) String ipv6;
}

@ActiveProfiles({Consts.AppProfiles.TEST})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TestBaseExcludeMvc {

}

@AutoConfigureMockMvc
public class ValidationTests extends TestBaseExcludeMvc {

  @Test
  public void test() {
    {

      var json =
          // @formatter:off
          "{\n"
              + "  \"cidr\": \"1.1.1.1/12\"\n"
              + "}";
      // @formatter:on
      JsonValidationUtil.parseAndValidateJson(json, Entity.class);
    }
    {

      Assertions.assertThrows(ConstraintViolationException.class, () -> {
        var json =
            // @formatter:off
            "{\n"
                + "  \"cidr\": \"1.1.1.1\"\n"
                + "}";
        // @formatter:on
        JsonValidationUtil.parseAndValidateJson(json, Entity.class);
      });
    }
  }

  @Test
  public void test1() {
    {

      var json =
          // @formatter:off
          "{\n"
              + "  \"ip_obj\": {\n"
              + "    \"is_ipv4\": true,\n"
              + "    \"cidr\": \"1.1.1.1/12\"\n"
              + "  }\n"
              + "}";
      // @formatter:on
      JsonValidationUtil.parseAndValidateJson(json, Entity1.class);
    }
    {

      Assertions.assertThrows(ConstraintViolationException.class, () -> {
        var json =
            // @formatter:off
            "{\n"
                + "  \"ip_obj\": {\n"
                + "    \"is_ipv4\": true,\n"
                + "    \"cidr\": \"1.1.1.1\"\n"
                + "  }\n"
                + "}";
        // @formatter:on
        JsonValidationUtil.parseAndValidateJson(json, Entity1.class);
      });
    }
  }

  @Test
  public void test2() {
    {
      var json =
          // @formatter:off
          "{\n"
              + "   \"ip\": \"1.1.1.1\",\n"
              + "   \"ipv4\": \"1.1.1.1\",\n"
              + "   \"ipv6\": \"2001:db8:1::ab9:C0A8:102\"\n"
              + "  }\n"
              + "}";
      // @formatter:on
      JsonValidationUtil.parseAndValidateJson(json, Entity2.class);
    }

    {
      Assertions.assertThrows(ConstraintViolationException.class, () -> {
        var json =
            // @formatter:off
            "{\n"
                + "   \"ip\": \"1.1.1\",\n"
                + "   \"ipv4\": \"1.1.1.1\",\n"
                + "   \"ipv6\": \"2001:db8:1::ab9:C0A8:102\"\n"
                + "  }\n"
                + "}";
        // @formatter:on
        JsonValidationUtil.parseAndValidateJson(json, Entity2.class);
      });
    }

    {
      Assertions.assertThrows(ConstraintViolationException.class, () -> {
        var json =
            // @formatter:off
            "{\n"
                + "   \"ip\": \"1.1.1.1\",\n"
                + "   \"ipv4\": \"1.1.1\",\n"
                + "   \"ipv6\": \"2001:db8:1::ab9:C0A8:102\"\n"
                + "  }\n"
                + "}";
        // @formatter:on
        JsonValidationUtil.parseAndValidateJson(json, Entity2.class);
      });
    }

    {
      Assertions.assertThrows(ConstraintViolationException.class, () -> {
        var json =
            // @formatter:off
            "{\n"
                + "   \"ip\": \"1.1.1.1\",\n"
                + "   \"ipv4\": \"1.1.1.1\",\n"
                + "   \"ipv6\": \"HAHAHA:db8:1::ab9:C0A8:102\"\n"
                + "  }\n"
                + "}";
        // @formatter:on
        JsonValidationUtil.parseAndValidateJson(json, Entity2.class);
      });
    }
  }
}
