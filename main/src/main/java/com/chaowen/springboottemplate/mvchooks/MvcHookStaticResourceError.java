package com.chaowen.springboottemplate.mvchooks;

import cn.hutool.core.util.ReflectUtil;
import com.chaowen.springboottemplate.base.auxiliry.JsonReqBodyCoercions.JsonDeserializeException;
import com.chaowen.springboottemplate.base.common.AppResponses.CommonErrCodes;
import com.chaowen.springboottemplate.base.common.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.common.Exceptions;
import com.chaowen.springboottemplate.base.common.Exceptions.BackendException;

import static com.chaowen.springboottemplate.base.common.JsonUtil.JSON_FORMAT_ERROR_MSG;

import com.chaowen.springboottemplate.base.common.SimpleFactories;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@Component
public class MvcHookStaticResourceError {

  @Autowired
  private CommonErrCodes commonErrCodes;

  public JsonResult exceptionHappened(   HttpServletRequest req, HttpServletResponse resp, Exception ex) {
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
      return JsonResult.of(null, "METHOD_NOT_ALLOWED", "");
    }

    if (ex instanceof NoHandlerFoundException) {
      resp.setStatus(404);
      resp.setHeader("Content-Type", "application/json");
      return null;
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
        return JsonResult.of(null, commonErrCodes.serverErr());
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
          String.format("parameter type mismatch: %s should be %s", name, type));
    }

    return helperFunctions.getServerErrorResult();
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

    @Autowired
    private CommonErrCodes commonErrCodes;

    JsonResult getParamValidationResult(
        @NotNull String msg) {
      return createJsonResult(commonErrCodes.paramError().getCode(),
          commonErrCodes.paramError().getMsg(),
          SimpleFactories.ofJson(Exceptions.PARAM_VIOLATION_KEY, msg));
    }

    JsonResult getParamValidationResult(
        @NotNull List<String> msgs) {
      return createJsonResult(commonErrCodes.paramError().getCode(),
          commonErrCodes.paramError().getMsg(),
          SimpleFactories.ofJson(Exceptions.PARAM_VIOLATION_KEY, msgs));
    }

    JsonResult getServerErrorResult() {
      return createJsonResult(commonErrCodes.serverErr().getCode(),
          commonErrCodes.serverErr().getMsg(), null);
    }

    @NotNull JsonResult getResult(
        @NotNull String code, @NotNull String msg, Object data) {
      return createJsonResult(code, msg, data);
    }

    // Common method for creating JsonResult
    private JsonResult createJsonResult(
        String code, String msg, Object data) {
      log.error("response with error: {}", code);
      return JsonResult.of(data, code, msg);
    }
  }

}
