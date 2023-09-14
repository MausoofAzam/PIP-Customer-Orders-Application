package com.snort.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidPageRequestException.class)
    public ResponseEntity<CustomerErrorResponse> paginationErrorHandler(InvalidPageRequestException e){
        CustomerErrorResponse response = new CustomerErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(value = CustomerCreationException.class)
//    public ResponseEntity<CustomerErrorResponse> todosErrorHandler(CustomerCreationException ex) {
//        log.error(ex.getMessage());
//        CustomerErrorResponse response = new CustomerErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(value = CustomerCreationException.class)
    public ResponseEntity<CustomerErrorResponse> handleCustomerCreationException(CustomerCreationException ex) {
        log.error(ex.getMessage());

        CustomerErrorResponse response = new CustomerErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<CustomerErrorResponse> resourceNotFoundException(CustomerNotFoundException ex) {
    log.error(ex.getMessage());
    CustomerErrorResponse response = new CustomerErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}

}