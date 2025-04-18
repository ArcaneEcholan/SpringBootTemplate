package com.chaowen.springboottemplate.mvchooks;

import static com.chaowen.springboottemplate.base.common.JsonUtil.JSON_FORMAT_ERROR_MSG;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.AppResponses.RespCode;
import com.chaowen.springboottemplate.base.JsonReqBodyCoercions.JsonDeserializeException;
import com.chaowen.springboottemplate.base.common.CustomErrorController.WebCtxException;
import com.chaowen.springboottemplate.base.common.JsonUtil;
import com.chaowen.springboottemplate.base.common.SimpleFactories;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@Component
public class MvcHookException {

  @SneakyThrows
  public ResponseEntity exceptionHappened(
      HttpServletRequest req, HttpServletResponse resp, Exception ex) {
    log.debug("== handle exception ==");

    var url = "";
    var method = "";
    if (req != null) {
      method = req.getMethod();
      url = req.getRequestURI();
    }
    log.error("{}: Server Exception-Name:{}: ，Server Exception-Msg: {}",
        method + "-" + url, ex.getClass().getSimpleName(), ex.getMessage());

    if (ex instanceof HttpRequestMethodNotSupportedException) {
      return ResponseEntity.ok(JsonResult.of(null, "METHOD_NOT_ALLOWED", ""));
    }

    if (ex instanceof BackendException) {
      var me = (BackendException) ex;
      return helperFunctions.getResult(me.getCode(), me.getMsg(), me.getData());
    }

    // controller param checking error
    if (ex instanceof ConstraintViolationException) {
      var me = (ConstraintViolationException) ex;

      List<String> violations = new ArrayList<String>();
      me.getConstraintViolations().forEach(constraintViolation -> {
        // get last path node
        String wholePath = constraintViolation.getPropertyPath().toString();
        violations.add(wholePath + ": " + constraintViolation.getMessage());
      });

      return helperFunctions.getParamValidationResult(violations);
    }
    // controller param checking error
    if (ex instanceof BindException ||
        ex instanceof MethodArgumentNotValidException) {
      BindingResult bindingResult = null;
      try {
        bindingResult =
            (BindingResult) ReflectUtil.getFieldValue(ex, "bindingResult");
      } catch (Throwable e) {
        log.error("fail to get binding result field of Class: {}",
            ex.getClass().getName());
        return ResponseEntity.ok(
            JsonResult.of(null, RespCodeImpl.SERVER_ERROR));
      }
      Objects.requireNonNull(bindingResult);

      List<String> violations = new ArrayList<>();
      bindingResult.getAllErrors().forEach(objectError -> {
        FieldError fieldError = (FieldError) objectError;
        violations.add(
            fieldError.getField() + ": " + fieldError.getDefaultMessage());
      });

      return helperFunctions.getParamValidationResult(violations);
    }
    if (ex instanceof MissingServletRequestParameterException  /*@RequestParam(required = true)*/ ||
        ex instanceof MissingServletRequestPartException) {

      if (ex instanceof MissingServletRequestParameterException) {
        var me = (MissingServletRequestParameterException) ex;

        var violationList = new ArrayList<Violation>();
        violationList.add(new Violation() {{
          this.setKey(me.getParameterName());
          this.setMsg("parameter is required");
        }});

        List<String> resultMsgs = new ArrayList<>();
        {
          for (Violation constraintViolation : violationList) {
            // get last path node
            resultMsgs.add(constraintViolation.getKey() + ": " +
                           constraintViolation.getMsg());
          }
        }
        return helperFunctions.getParamValidationResult(resultMsgs);
      }

      return helperFunctions.getParamValidationResult("api parameter missing");
    }
    if (ex instanceof HttpMessageNotReadableException) {
      var e = (HttpMessageNotReadableException) ex;
      var mostSpecificCause = e.getMostSpecificCause();
      if (mostSpecificCause instanceof JsonDeserializeException) {
        return helperFunctions.getParamValidationResult(
            mostSpecificCause.getMessage());
      }
      return helperFunctions.getParamValidationResult(JSON_FORMAT_ERROR_MSG);
    }
    if (ex instanceof MaxUploadSizeExceededException) {
      return helperFunctions.getParamValidationResult(
          "upload file size exceeding upper bound");
    }
    if (ex instanceof MethodArgumentTypeMismatchException) {
      var name = ((MethodArgumentTypeMismatchException) ex).getName();
      var type = ((MethodArgumentTypeMismatchException) ex).getRequiredType()
          .getSimpleName();
      return helperFunctions.getParamValidationResult(
          String.format("parameter type mismatch: %s should be %s", name,
              type));
    }

    // if any other exception not listed above occurs and tomcat catches
    // it, you can handle it here, for example, when spring throws a handler_mapping_not_found,
    // or there are other errors thrown out from inner tomcat,
    // you have the right to decide what to return.
    if (ex instanceof WebCtxException) {
      int statusCode = resp.getStatus();

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        // deal with api not found
        if (req.getRequestURI().startsWith("/api")) {
          return ResponseEntity.notFound().build();
        }

        // deal with static not found (especially useful for SPA)
        var pageHtml = "404";
        var in = new ClassPathResource("/static/index.html").getInputStream();
        if (in != null) {
          pageHtml = IoUtil.readUtf8(in);
        }
        return returnHtml(pageHtml);
      }
    }

    return helperFunctions.getServerErrorResult();
  }

  private ResponseEntity returnHtml(@NotNull String html) {
    var headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
    return new ResponseEntity(html, headers, HttpStatus.OK);
  }

  @Data
  static class Violation {

    String key;
    String msg;
  }

  @Autowired
  HelperFunctions helperFunctions;

  @Component
  public static class HelperFunctions {

    ResponseEntity<JsonResult> getParamValidationResult(
        @NotNull String msg) {
      return createJsonResult(RespCodeImpl.PARAMS_ERROR.getCode(),
          RespCodeImpl.PARAMS_ERROR.getMsg(),
          SimpleFactories.ofJson(PARAM_VIOLATION_KEY, msg));
    }

    ResponseEntity<JsonResult> getParamValidationResult(
        @NotNull List<String> msgs) {
      return createJsonResult(RespCodeImpl.PARAMS_ERROR.getCode(),
          RespCodeImpl.PARAMS_ERROR.getMsg(),
          SimpleFactories.ofJson(PARAM_VIOLATION_KEY, msgs));
    }

    ResponseEntity<JsonResult> getServerErrorResult() {
      return createJsonResult(RespCodeImpl.SERVER_ERROR.getCode(),
          RespCodeImpl.SERVER_ERROR.getMsg(), null);
    }

    ResponseEntity<JsonResult> getResult(
        @NotNull String code, @NotNull String msg, Object data) {
      return createJsonResult(code, msg, data);
    }

    // Common method for creating JsonResult
    private ResponseEntity<JsonResult> createJsonResult(
        String code, String msg, Object data) {
      log.error("response with error: {}", code);
      return ResponseEntity.ok(JsonResult.of(data, code, msg));
    }
  }

  @Data
  public static class BackendException extends RuntimeException {

    private Object data;

    private String code;

    private String msg;

    public BackendException(Object data, RespCode centerRespCode) {
      super(centerRespCode.getCode());
      this.data = data;
      this.code = centerRespCode.getCode();
      this.msg = centerRespCode.getMsg();
    }

    public BackendException(Object data, String code, String msg) {
      super(code);
      this.data = data;
      this.code = code;
      this.msg = msg;
    }

    @Override
    public String toString() {
      return JsonUtil.toJsonString(
          SimpleFactories.ofJson("code", code, "msg", msg, "data", data));
    }
  }

  public static final String PARAM_VIOLATION_KEY = "violations";
}
