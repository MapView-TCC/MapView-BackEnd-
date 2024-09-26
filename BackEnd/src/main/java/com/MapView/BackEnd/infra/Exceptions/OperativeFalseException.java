package com.MapView.BackEnd.infra.Exceptions;

public class OperativeFalseException extends RuntimeException {
    public OperativeFalseException(String message){
        super(message);
    }
}
