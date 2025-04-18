package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.common.DatabaseSqls.DatabaseSqlMapper;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.Index;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DbIndexCreator {

  @Autowired
  DatabaseSqlMapper databaseSqlMapper;

  public void createIdxs(List<Index> idxList) {
    idxList.forEach(index -> {
      var r = Utils.trycatch(
              () -> databaseSqlMapper.showIndexes(index.getTableName()))
          .ifFailed(it -> {
            log.warn("create index failed, table not exist: {}", index);
          }).throwIfEx();

      var indexInfos = r.getValue();
      var keyNames = indexInfos.stream().filter(
              it -> it.containsKey("Key_name") &&
                    Objects.equals(it.get("Key_name"), index.getName()))
          .collect(Collectors.toList());
      if (keyNames.isEmpty()) {
        {
          var cols = index.getOrderedColumns();

          cols = cols.stream().map(it -> {
            if (!it.startsWith("`")) {
              return Utils.fmt("{}{}{}", '`', it, '`');
            }
            return it;
          }).collect(Collectors.toList());

          // convert List to comma-separated string
          var columns = String.join(", ", cols);

          // pass the parameters to the MyBatis mapper
          databaseSqlMapper.createIndex(index.getName(), index.getTableName(),
              columns);
        }
        log.debug("index created: {}", index);
      } else {
        log.debug("index exist, skip: {}", index);
      }
    });
  }

}
