package com.ayd.library.exception;

public class DuplicatedEntityException extends ServiceException{
    public DuplicatedEntityException(){

    }
    public DuplicatedEntityException(String message){
        super(message);
    }
}
