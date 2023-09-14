package com.snort.exception;

public class InvalidPageRequestException extends RuntimeException{
    public InvalidPageRequestException(String message) {
        super(message);
    }
}
