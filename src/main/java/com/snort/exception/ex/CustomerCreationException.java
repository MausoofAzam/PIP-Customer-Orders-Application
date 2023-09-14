package com.snort.exception.ex;

public class CustomerCreationException extends RuntimeException{
    public CustomerCreationException(String message) {
        super(message);
    }
}
