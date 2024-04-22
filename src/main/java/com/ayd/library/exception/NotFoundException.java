package com.ayd.library.exception;

public class NotFoundException extends ServiceException{
    public NotFoundException(){

    }

    public NotFoundException(String message){
        super(message);
    }
}
