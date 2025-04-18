package com.chaowen.springboottemplate.base.testsbase;


import com.chaowen.springboottemplate.base.common.JsonUtil;
import java.io.File;
import java.util.List;
import lombok.Data;
import lombok.var;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonUtilTest {

  @Test
  void test() {
    // to string
    var str = JsonUtil.toJsonString(new A());
    Assertions.assertTrue(str.contains("null"));
    str = JsonUtil.toJsonString(null);
    str = JsonUtil.toJsonString(new File(""));
    Assertions.assertThrows(Exception.class, () -> {
      JsonUtil.toJsonString(new B());
    });

    // parse
    Assertions.assertDoesNotThrow(() -> {
      var json = "{\"name\": \"john\", \"lower_camel_field\": \"value\"}";
      var parse = JsonUtil.parse(json, A.class);
      Assertions.assertNotNull(parse);
    });

    // parse
    Assertions.assertDoesNotThrow(() -> {
      var json = "{\"name\": \"john\"}";
      var parse = JsonUtil.parse(json, A.class);
    });
    Assertions.assertThrows(Exception.class, () -> {
      var json = "{\"name\": \"john}";
      var parse = JsonUtil.parse(json, A.class);
    });

    // parse null
    var json = "{\"name\": \"john\", \"field\": {}}";
    var parse = JsonUtil.parse(json, A.class);
    Assertions.assertNull(parse.getField());
    Assertions.assertNull(parse.getList());
    Assertions.assertNull(parse.getNest());
    Assertions.assertNull(parse.getLowerCamelField());

    // parse list
    json = "{\"name\": \"john\", \"list\": [{\"name\": \"john\"}]}";
    parse = JsonUtil.parse(json, A.class);
    Assertions.assertEquals(1, parse.getList().size());

    // parse nest
    json = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
    parse = JsonUtil.parse(json, A.class);
    Assertions.assertNotNull(parse.getNest());

    // parse list
    json = "[{\"name\": \"john\"}]";
    var parsea = JsonUtil.parseList(json, A.class);
    Assertions.assertEquals(1, parsea.size());

    Assertions.assertThrows(Exception.class, () -> {
      var json1 = "{\"name\": \"john\"}";
      var parsea1 = JsonUtil.parseList(json1, A.class);
      Assertions.assertNotNull(parsea1);
    });

    json = "[{\"name\": \"john\"}]";
    var parsea2 = JsonUtil.parseList(json);
    Assertions.assertEquals(1, parsea2.size());

    // obj to json, reserve null
    var a = new A();
    var j = JsonUtil.objToJsonObj(a);
    Assertions.assertEquals(4, j.size());

    // path read
    Assertions.assertThrows(Exception.class, () -> {
      var jsona = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
      var read = JsonUtil.jsonPathRead(jsona, "nest");
      Assertions.assertNotNull(read);
    });

    {
      var jsona = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
      var read = JsonUtil.jsonPathRead(jsona, "$.name.b");
      Assertions.assertNull(read);
    }

    json = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
    var read = JsonUtil.jsonPathRead(json, "$.nest");
    Assertions.assertNotNull(read);

    // path read
    json = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
    var reada = JsonUtil.jsonPathRead(json, "$", A.class);
    Assertions.assertNotNull(reada);

    // path read list
    json = "[{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}]";
    var readlist = JsonUtil.jsonPathReadList(json, "$");
    Assertions.assertNotNull(readlist);

    json = "[{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}]";
    readlist = JsonUtil.jsonPathReadList(json, "$.a.b");
    Assertions.assertNull(readlist);

    json = "[{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}]";
    var pr = JsonUtil.jsonPathRead(json, "$[0].name", String.class);
    Assertions.assertNotNull(pr);

    Assertions.assertThrows(Exception.class, () -> {
      var jsona = "{\"name\": \"john\", \"nest\": {\"name\": \"john\"}}";
      var pra = JsonUtil.jsonPathRead(jsona, "$[0].name", String.class);
      Assertions.assertNotNull(pra);
    });
  }

  @Data
  static class A {

    String lowerCamelField;

    String field;

    List<A> list;

    A nest;
  }

  static class B {

    private String field;
  }

}

