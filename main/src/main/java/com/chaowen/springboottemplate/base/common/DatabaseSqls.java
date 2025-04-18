package com.chaowen.springboottemplate.base.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class DatabaseSqls {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Index {

    String tableName = "";
    String name = "";
    List<String> orderedColumns = new ArrayList<>();

    public void addColumn(String timestamp) {
      this.orderedColumns.add(timestamp);
    }

    public void setTableName(String tableName) {
      this.tableName = tableName;
    }

    public String getName() {
      if (name.isEmpty()) {
        return Utils.fmt("idx_{}_{}", tableName,
            String.join("_", orderedColumns.toArray(new String[]{})));
      }
      return name;
    }

    public String toString() {
      return Utils.fmt("Index(table={}, name={}, columns={})", getTableName(), getName(),
          getOrderedColumns());
    }
  }

  @Mapper
  public interface DatabaseSqlMapper {

    @Update("CREATE DATABASE IF NOT EXISTS `${dbName}` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci")
    void createDatabaseIfNotExists(String dbName);

    @Select("SHOW INDEXES FROM ${tableName}")
    List<Map<String, Object>> showIndexes(
        @Param("tableName")
        String tableName);

    @Update("CREATE INDEX ${indexName} " + "ON `${tableName}` (${columns})")
    void createIndex(
        @Param("indexName")
        String indexName,
        @Param("tableName")
        String tableName,
        @Param("columns")
        String columns);
  }

  @Service
  public static class DatabaseService {

    @Autowired
    private DatabaseSqlMapper databaseSqlMapper;

    public void createIndex(
        String indexName, String tableName, List<String> columnNames) {
      // convert List to comma-separated string
      var columns = String.join(", ", columnNames);

      // pass the parameters to the MyBatis mapper
      databaseSqlMapper.createIndex(indexName, tableName, columns);
    }
  }
}
