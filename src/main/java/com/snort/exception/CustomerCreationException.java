package com.snort.exception;

public class CustomerCreationException extends RuntimeException{
    public CustomerCreationException(String message) {
        super(message);
    }
}
