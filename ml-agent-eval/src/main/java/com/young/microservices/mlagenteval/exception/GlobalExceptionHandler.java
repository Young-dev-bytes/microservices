//package com.in28minutes.microservices.mlagenteval.exception;
//
//import com.in28minutes.microservices.mlagenteval.common.BaseResponseTemp;
//import com.in28minutes.microservices.mlagenteval.enums.SystemErrorEnum;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.connector.RequestFacade;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.jdbc.BadSqlGrammarException;
//import org.springframework.validation.BindException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//
//import javax.validation.ConstraintViolationException;
//import javax.validation.ValidationException;
//import java.io.UnsupportedEncodingException;
//import java.sql.SQLIntegrityConstraintViolationException;
//import java.sql.SQLSyntaxErrorException;
//
///**
// * @author SeiYa Jiang
// * @version 1.0
// * @description: 全局异常处理
// * @date 2021/7/7 13:42
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public BaseResponseTemp methodArgumentNotValidException(MethodArgumentNotValidException e, RequestFacade request) {
//        FieldError error = e.getBindingResult().getFieldError();
//        log.error("参数校验异常:{}", e.getMessage());
//        return BaseResponseTemp.fail("-1", error.getDefaultMessage());
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public BaseResponseTemp handleValidationException(ValidationException e, RequestFacade request) {
//        log.error("参数异常:{}", e.getMessage());
//        return BaseResponseTemp.fail("-1", e.getCause().getMessage());
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public BaseResponseTemp handleConstraintViolationException(ConstraintViolationException e, RequestFacade request) {
//        log.error("参数校验异常:{}", e.getMessage());
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//    @ExceptionHandler(BindException.class)
//    public BaseResponseTemp bindException(BindException e, RequestFacade request) {
//        log.error("参数绑定异常:{}", e.getMessage());
//        return BaseResponseTemp.fail("-1", e.getAllErrors().get(0).getDefaultMessage());
//    }
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public BaseResponseTemp processException(MaxUploadSizeExceededException e, RequestFacade request) {
//        log.error("上传文件超过限制:{}", e.getMessage());
//        return BaseResponseTemp.fail("-1", "Maximum upload size exceeded(20MB)");
//    }
//
//
//   /* @ExceptionHandler(BusinessException.class)
//    public BaseResponseTemp businessException(BusinessException businessException, RequestFacade request) {
//        log.error("业务异常: errorCode:[" + businessException.getErrorCode() + "], errorMsg:[" + businessException.getErrorMsg() + "]", businessException);
//        return BaseResponseTemp.fail(businessException.getErrorCode(), businessException.getErrorMsg());
//    }*/
//
//    @ExceptionHandler(BusinessException.class)
//    public BaseResponseTemp businessException(BusinessException businessException, RequestFacade request) {
//        log.error("业务异常: errorCode:[" + businessException.getErrorCode().getCode() + "], errorMsg:[" + businessException.getErrorCode().getMessage() + "]", businessException);
//        return BaseResponseTemp.fail(businessException.getErrorCode().getCode(), businessException.getErrorCode().getMessage());
//    }
//
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public BaseResponseTemp exception(SQLIntegrityConstraintViolationException exception, RequestFacade request) {
//        log.error("sql调用异常: ", exception);
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//    @ExceptionHandler(BadSqlGrammarException.class)
//    public BaseResponseTemp exception(SQLSyntaxErrorException exception, RequestFacade request) {
//        log.error("sql执行异常: ", exception);
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//    @ResponseBody
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public BaseResponseTemp sqlException(DataIntegrityViolationException exception, RequestFacade request) {
//        log.error("sql调用异常: ", exception);
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//    @ExceptionHandler(UnsupportedEncodingException.class)
//    public BaseResponseTemp unSupportException(UnsupportedEncodingException exception, RequestFacade request) {
//        log.error("字符格式异常:", exception);
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public BaseResponseTemp exception(Exception exception, RequestFacade request) {
//        log.error("系统内部处理异常", exception);
//        return BaseResponseTemp.fail(SystemErrorEnum.COMMON_UNKNOWN_ERROR);
//    }
//
//}
