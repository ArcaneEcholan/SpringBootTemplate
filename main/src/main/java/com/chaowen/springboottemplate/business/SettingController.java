package com.chaowen.springboottemplate.business;

import static com.chaowen.springboottemplate.base.AppResponses.JsonResult.ok;

import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.ConditionalOnDbEnabled;
import com.chaowen.springboottemplate.business.SettingsDaos.SettingsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@ConditionalOnDbEnabled
public class SettingController {

  @Autowired
  SettingsDao settingsDao;

  @GetMapping("/settings")
  public JsonResult getSettings() {
    return ok(settingsDao.all());
  }

}
