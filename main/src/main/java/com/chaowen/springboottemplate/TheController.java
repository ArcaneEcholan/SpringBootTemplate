package com.chaowen.springboottemplate;

import static com.chaowen.springboottemplate.base.common.AppResponses.JsonResult.ok;

import com.chaowen.springboottemplate.base.common.AppResponses.JsonResult;
import com.chaowen.springboottemplate.settingstorage.SettingsDaos.SettingsDao;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class TheController {

  @Autowired
  SettingsDao settingsDao;

  @GetMapping("/settings")
  public JsonResult getSettings() {
    return ok(settingsDao.all());
  }


  @GetMapping("/settings2")
  public ResponseEntity<JsonResult> getSe1tt2ings() {
    return ResponseEntity.ok(ok(settingsDao.all()));
  }

  @GetMapping("/settings3")
  public ResponseEntity get2Se1tt2ings(HttpServletResponse httpResponse)
      throws IOException {
    httpResponse.sendError(404);
    return ResponseEntity.notFound().build();
  }
}
