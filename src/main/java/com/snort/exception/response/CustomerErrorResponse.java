package com.snort.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerErrorResponse {
        private HttpStatus httpStatus;
        private String message;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object data;


    public CustomerErrorResponse(int value, String message, Object data) {

    }
}
