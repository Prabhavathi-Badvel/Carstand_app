package com.my.fl.startup.model;

import lombok.Data;

@Data
public class ResponseMessageDto {
    private String message;
    
    private boolean status;
    
    private Object data;
}
