package com.base.web;


import com.kucoin.base.exception.BaseException;
import com.kucoin.base.exception.CommonErrorCode;
import com.kucoin.base.exception.ErrorCode;
import com.kucoin.base.result.GenericResult;
import java.lang.reflect.Method;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> defaultErrorHandler(HttpServletRequest req, Exception ex) {
        if (BaseException.class.isAssignableFrom(ex.getClass())) {
            BaseException baseEx = (BaseException)ex;
            ErrorCode errorCode = (ErrorCode)Optional.ofNullable(baseEx.getErrorCode()).orElse(CommonErrorCode.INNER_ERROR);
            Class aClass = LOG.getClass();

            try {
                Method declaredMethod = aClass.getDeclaredMethod(errorCode.getErrorLevel().name().toLowerCase(), String.class, Object.class, Object.class);
                declaredMethod.invoke(LOG, "catch global exception with url:{}", req.getRequestURI(), ex);
            } catch (Exception var8) {
                LOG.error("catch global exception with url:{}", req.getRequestURI(), ex);
            }

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            switch(errorCode.getErrorType()) {
                case BIZ:
                    status = HttpStatus.OK;
                    break;
                case PARAMETER:
                    status = HttpStatus.BAD_REQUEST;
            }

            GenericResult result = GenericResult.fail(errorCode.getCode(), errorCode.isRetry(), new String[]{StringUtils.isBlank(baseEx.getMessage()) ? errorCode.getDescription() : baseEx.getMessage()});
            result.setArgs(baseEx.getArgs());
            return new ResponseEntity(result, (MultiValueMap)null, status);
        } else {
            HttpStatus status = this.handleMvcExceptions(ex);
            if (status.is5xxServerError()) {
                LOG.error("catch global exception with url:{}", req.getRequestURI(), ex);
            } else {
                LOG.warn("catch global exception with url:{}", req.getRequestURI(), ex);
            }

            return new ResponseEntity(GenericResult.fail(String.valueOf(status.value()), new String[]{status.getReasonPhrase()}), status);
        }
    }

    private HttpStatus handleMvcExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            status = HttpStatus.NOT_ACCEPTABLE;
        } else if (ex instanceof MissingPathVariableException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof MissingServletRequestParameterException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ServletRequestBindingException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof ConversionNotSupportedException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof TypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof HttpMessageNotReadableException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof HttpMessageNotWritableException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof MissingServletRequestPartException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof BindException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof NoHandlerFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof AsyncRequestTimeoutException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }

        return status;
    }
}

