package com.chaowen.springboottemplate.base.common;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.update.Update;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class SqlValidator {

  List<String> splitStatements(String sql) {
    var statements = new ArrayList<String>();
    var statement = new StringBuilder();
    for (String line : sql.split("\n")) {
      // skip comments and empty lines
      if (line.trim().isEmpty() || line.trim().startsWith("--")) {
        continue;
      }

      statement.append(line.trim());
      if (line.trim().endsWith(";")) { // end of statement
        statements.add(statement.toString());
        statement.setLength(0); // reset the builder
      }
    }

    // add the last statement if there's no trailing semicolon
    if (statement.length() > 0) {
      statements.add(statement.toString());
    }

    return statements;
  }

  public boolean isSchemaSafe(String schema) {
    var sqlStatements = splitStatements(schema);
    for (String sql : sqlStatements) {
      if (!validateSingleStatement(sql)) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {

  }

  public static boolean validateSingleStatement(@NotNull String sql) {
    try {
      var statement = CCJSqlParserUtil.parse(sql);

      // disallow UPDATE, DELETE, DROP
      if (statement instanceof Update || statement instanceof Delete ||
          statement instanceof Drop) {
        log.error("Unsafe SQL statement: {}", sql);
        return false;
      }

      // add more cases for other SQL types if needed
    } catch (Exception e) {
      log.error("unrecognized sql: {}", sql);
      // parsing failed; assume unsafe
      return false;
    }

    // if no valid cases, reject by default
    return false;
  }

  private static  boolean isBasicPlatformTable(String tableName) {
    // check if the table is part of basicplatform's schema
    return tableName.startsWith("basic_"); // example prefix-based check
  }
}
