package com.snort.exception.global;

import com.snort.exception.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class InputValidationExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setTimeStamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
//        errorResponse.setError(status.name());
//        StringBuffer requestURL = ((ServletWebRequest) request).getRequest().getRequestURL();
//        errorResponse.setPath(String.valueOf(requestURL));
//        error fields
        List<FieldError>  fieldErrors = ex.getFieldErrors();
        List<String > errors = new ArrayList<>();
        fieldErrors.forEach(fieldError -> {
            errors.add(fieldError.getDefaultMessage());
        });
        errorResponse.setErrors(errors);
        return new ResponseEntity<>(errorResponse,headers,status);
    }
}
