package com.MapView.BackEnd.infra;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PostsErrorException extends RuntimeException{
    public PostsErrorException(String message){
        super(message);
    }
}
