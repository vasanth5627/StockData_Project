package org.example.StockExceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<String,String> handleExceptions(Exception ex){
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("Error",ex.getMessage());
        return errorMap;
    }

}
