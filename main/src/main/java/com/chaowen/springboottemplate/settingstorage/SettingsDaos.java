package com.chaowen.springboottemplate.settingstorage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.var;
import org.apache.ibatis.annotations.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

public class SettingsDaos {

  @Mapper
  public interface SettingsMapper extends BaseMapper<SettingsPo> {

  }

  @Data
  @TableName("`settings`")
  public static class SettingsPo implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("`key`")
    private String key;

    @TableField("`value`")
    private String value;
  }

  @Repository
  public static class SettingsDao  {

    @Autowired
    private SettingsMapper settingsMapper;

    public List<SettingsPo> all() {
        return settingsMapper.selectList(null);
    }

    @Nullable
    public String get(@NotNull String key) {
      var settingPos = settingsMapper.selectList(
          Wrappers.<SettingsPo>lambdaQuery().eq(SettingsPo::getKey, key));
      if (settingPos.isEmpty()) {
        return null;
      }

      var settingPo = settingPos.get(0);
      return settingPo.getValue();
    }

    public void set(@NotNull String key, @NotNull String value) {
      var settingPos = settingsMapper.selectList(
          Wrappers.<SettingsPo>lambdaQuery().eq(SettingsPo::getKey, key));
      if (settingPos.isEmpty()) {
        var ttnConfig = new SettingsPo();
        ttnConfig.setKey(key);
        ttnConfig.setValue(value);
        settingsMapper.insert(ttnConfig);
      } else {
        var settingPo = settingPos.get(0);
        settingsMapper.update(null,
            Wrappers.<SettingsPo>lambdaUpdate().set(SettingsPo::getKey, key)
                .set(SettingsPo::getValue, value)
                .eq(SettingsPo::getId, settingPo.getId()));
      }
    }
  }
}
