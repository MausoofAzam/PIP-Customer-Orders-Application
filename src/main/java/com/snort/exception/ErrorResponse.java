package com.snort.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

//    private LocalDateTime timeStamp;
    private int status;
//    private String error;
//    private String path;
    private List<String > errors;


}
