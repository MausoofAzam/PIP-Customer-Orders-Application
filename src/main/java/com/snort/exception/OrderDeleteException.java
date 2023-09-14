package com.snort.exception;

public class OrderDeleteException extends RuntimeException{
    public OrderDeleteException(String message) {
        super(message);
    }
}
