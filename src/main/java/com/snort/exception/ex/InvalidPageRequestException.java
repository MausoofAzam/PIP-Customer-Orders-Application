package com.snort.exception.ex;

public class InvalidPageRequestException extends RuntimeException{
    public InvalidPageRequestException(String message) {
        super(message);
    }
}
