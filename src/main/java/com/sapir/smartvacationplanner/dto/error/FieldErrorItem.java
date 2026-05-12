package com.sapir.smartvacationplanner.dto.error;

public class FieldErrorItem {

    private String field;
    private String error;

    public FieldErrorItem(){

    }

    public FieldErrorItem (String field, String error)
    {
        this.field = field;
        this.error = error;

    }

    public String getField(){
        return field;
    }

    public String getError (){
        return error;
    }

    public void setField(String field){
        this.field = field;
    }

    public void setError (String error){
        this.error = error;
    }


}