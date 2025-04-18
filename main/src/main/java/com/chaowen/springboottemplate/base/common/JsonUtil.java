package com.chaowen.springboottemplate.base.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

  public static final String JSON_FORMAT_ERROR_MSG = "json format invalid";

  @Getter
  static final ObjectMapper om;

  @NotNull
  private static final Logger log =
      Objects.requireNonNull(LoggerFactory.getLogger(JsonUtil.class));

  static {
    // don't use spring, it'll mess up with the default ObjectMapper
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        false);
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

    om = objectMapper;
  }

  @SuppressWarnings("unchecked")
  public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return getOm().convertValue(fromValue, toValueType);
  }

  @SneakyThrows
  @NotNull
  public static String toJsonString(Object obj) {
    try {
      return om.writeValueAsString(obj);
    } catch (InvalidDefinitionException e) {
      throw new RuntimeException(
          "properties of the object must has getter to be serialized: " +
          e.getMessage(), e);
    }
  }

  public static Map parse(String jsonString) {
    return parse(jsonString, Map.class);
  }

  public static <T> T parse(String jsonString, Class<T> clazz) {
    try {
      return om.readValue(jsonString, clazz);
    } catch (Exception e) {
      throw new RuntimeException(JSON_FORMAT_ERROR_MSG, e);
    }
  }

  public static <T> T parseObject(String jsonString, Class<T> clazz) {
    return parse(jsonString, clazz);
  }

  public static Map parseObject(String jsonString) {
    return parse(jsonString);
  }


  public static Map objToJsonObj(Object json) {
    return parse(toJsonString(json));
  }

  public static <T> T objToJsonObj(Object json, Class<T> clazz) {
    return parse(toJsonString(json), clazz);
  }

  @SneakyThrows
  public static <T> List<T> parseList(String json, Class<T> clazz) {
    return om.readValue(json,
        om.getTypeFactory().constructCollectionType(List.class, clazz));
  }

  @SneakyThrows
  public static List parseList(String json) {
    return om.readValue(json, List.class);
  }

  public static Object jsonPathRead(String json, String path) {
    try {
      // Ensure the path starts with "$"
      if (!path.startsWith("$")) {
        throw new IllegalArgumentException("Path must start with '$'");
      }

      // Parse the JSON string into a JsonNode
      JsonNode currentNode = om.readTree(json);

      // Handle the special case where path is just "$"
      if ("$".equals(path)) {
        return convertNode(currentNode, null);
      }

      // Use a regex-based approach to parse the path
      Pattern pattern = Pattern.compile("([a-zA-Z0-9_-]+|\\[\\d+])");
      Matcher matcher = pattern.matcher(path.substring(1));

      while (matcher.find()) {
        String key = matcher.group();

        // Handle array indexing
        if (key.startsWith("[")) {
          // Extract the index from the array notation
          int index = Integer.parseInt(key.substring(1, key.length() - 1));
          if (currentNode != null && currentNode.isArray()) {
            currentNode = currentNode.get(index);
          } else {
            throw new RuntimeException(
                "The path does not point to a valid json array: " + key);
          }
        } else {
          if (currentNode == null) {
            return null;
          }
          // Navigate to the next node using the key
          currentNode = currentNode.get(key);
        }
      }

      // Convert the final node to the desired type
      return convertNode(currentNode, null);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading JSON path: " + path + " : " + e.getMessage(), e);
    }
  }

  public static <T> T jsonPathRead(String json, String path, Class<T> clazz) {
    try {
      // Ensure the path starts with "$"
      if (!path.startsWith("$")) {
        throw new IllegalArgumentException("Path must start with '$'");
      }

      // Parse the JSON string into a JsonNode
      JsonNode currentNode = om.readTree(json);

      // Handle the special case where path is just "$"
      if ("$".equals(path)) {
        return convertNode(currentNode, clazz);
      }

      // Use a regex-based approach to parse the path
      Pattern pattern = Pattern.compile("([a-zA-Z0-9_-]+|\\[\\d+])");
      Matcher matcher = pattern.matcher(path.substring(1));

      while (matcher.find()) {
        String key = matcher.group();

        // Handle array indexing
        if (key.startsWith("[")) {
          // Extract the index from the array notation
          int index = Integer.parseInt(key.substring(1, key.length() - 1));
          if (currentNode != null && currentNode.isArray()) {
            currentNode = currentNode.get(index);
          } else {
            throw new RuntimeException(
                "The path does not point to a valid json array: " + key);
          }
        } else {
          if (currentNode == null) {
            return null;
          }
          // Navigate to the next node using the key
          currentNode = currentNode.get(key);
        }
      }

      // Convert the final node to the desired type
      return convertNode(currentNode, clazz);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading JSON path: " + path + " : " + e.getMessage(), e);
    }
  }

  public static List jsonPathReadList(String json, String path) {
    return (List) jsonPathRead(json, path);
  }

  public static <T> List<T> jsonPathReadList(
      String json, String path, Class<T> clazz) {
    try {
      // Ensure the path starts with "$"
      if (!path.startsWith("$")) {
        throw new IllegalArgumentException("Path must start with '$'");
      }

      // Parse the JSON string into a JsonNode
      JsonNode currentNode = om.readTree(json);

      // Handle the special case where path is just "$"
      if ("$".equals(path)) {
        return (List<T>) convertNode(currentNode, clazz);
      }

      // Use a regex-based approach to parse the path
      Pattern pattern = Pattern.compile("([a-zA-Z0-9_-]+|\\[\\d+])");
      Matcher matcher = pattern.matcher(path.substring(1));

      while (matcher.find()) {
        String key = matcher.group();

        // Handle array indexing
        if (key.startsWith("[")) {
          // Extract the index from the array notation
          int index = Integer.parseInt(key.substring(1, key.length() - 1));
          if (currentNode != null && currentNode.isArray()) {
            currentNode = currentNode.get(index);
          } else {
            throw new RuntimeException(
                "The path does not point to a valid json array: " + key);
          }
        } else {
          if (currentNode == null) {
            return null;
          }
          // Navigate to the next node using the key
          currentNode = currentNode.get(key);
        }
      }

      // Convert the final node to the desired type
      return (List<T>) convertNode(currentNode, clazz);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading JSON path: " + path + " : " + e.getMessage(), e);
    }
  }

  private static <T> T convertNode(JsonNode node, Class<T> clazz) {
    if (node == null) {
      return null;
    }
    if (node.isArray()) {
      if (clazz == null) {
        return (T) om.convertValue(node, List.class);
      }
      return om.convertValue(node,
          om.getTypeFactory().constructCollectionType(List.class, clazz));
    } else {
      if (clazz == null) {
        return (T) om.convertValue(node, Object.class);
      }
      return om.convertValue(node, clazz);
    }
  }
}
