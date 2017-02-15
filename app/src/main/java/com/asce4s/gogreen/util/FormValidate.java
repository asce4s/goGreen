package com.asce4s.gogreen.util;

import android.widget.EditText;

/**
 * Created by Ace on 9/23/2016.
 */

public class FormValidate {

     public boolean validateReq(EditText et){
        if(et.getText().toString().isEmpty()){
            et.setError("Required");
            return false;
        }
        return true;
    }

     public boolean validateReq(EditText [] ets){

        boolean isValid=true;
        for (EditText et:ets) {
            if(et.getText().toString().isEmpty()){
                et.setError("Required");
                isValid=false;
            }
        }

        return isValid;
    }

    public boolean validateEmail(EditText et){
        boolean isEmail= android.util.Patterns.EMAIL_ADDRESS.matcher(et.getText().toString()).matches();
        if(!isEmail){
            et.setError("Enter a valid email");
            return false;
        }
        return true;

    }

    public boolean match(EditText et1,EditText et2,String err){
        if(!et1.getText().toString().equals(et2.getText().toString())){

            et2.setError(err);
            return false;
        }
        return true;
    }

    public boolean charCount(EditText et,int count){
        int chars=et.getText().toString().length();
        if(chars<count) {
            et.setError("Password must be minimum 6 characters");
            return false;
        }
        return true;
    }
}
