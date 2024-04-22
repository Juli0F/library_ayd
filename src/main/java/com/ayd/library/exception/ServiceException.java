package com.ayd.library.exception;

public class ServiceException extends Exception{
    public ServiceException(){

    }

    public ServiceException(String message){
        super(message);
    }
}
