package com.sapir.smartvacationplanner.dto.error;

import java.util.List;

public class ApiErrorResponse {

    private String message;
    private String path;
    private int status;
    private String timestamp;
    private List<FieldErrorItem> fieldErrors; //(nullable/optional)

    public ApiErrorResponse(){

    }

    public ApiErrorResponse(String message, String path, int status, String timestamp, 
                            List<FieldErrorItem> fieldErrors){
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;                               
    }

    public String getMessage(){
        return message;
    }

    public String getPath(){
        return path;
    }

    public int getStatus(){
        return status;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public List<FieldErrorItem> getFieldErrors(){
        return fieldErrors;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public void setFieldErrors(List<FieldErrorItem> fieldErrors){
        this.fieldErrors = fieldErrors;
    }




}