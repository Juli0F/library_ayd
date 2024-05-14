package com.ayd.library.exception;

public class RequiredEntityException extends ServiceException{
    public RequiredEntityException(){

    }
    public RequiredEntityException(String message){
        super(message);
    }
}
