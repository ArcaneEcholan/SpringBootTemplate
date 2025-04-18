package com.chaowen.springboottemplate.business;

import static com.chaowen.springboottemplate.base.AppResponses.JsonResult.ok;

import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.business.SettingsDaos.SettingsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class SettingController {

  @Autowired
  SettingsDao settingsDao;

  void m() {
    throw new RuntimeException();
  }

  @GetMapping("/settings")
  @Transactional
  public JsonResult getSettings() {
    return ok(settingsDao.all());
  }

}
