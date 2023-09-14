package com.snort.exception.ex;

public class PhoneNumberExistException extends RuntimeException{
    public PhoneNumberExistException(String message) {
        super(message);
    }
}
