package com.MapView.BackEnd.infra.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlankErrorException extends RuntimeException{
    public BlankErrorException(String message){
        super(message);
    }
}
