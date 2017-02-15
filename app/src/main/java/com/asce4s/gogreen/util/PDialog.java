package com.asce4s.gogreen.util;


import android.app.ProgressDialog;
import android.content.Context;

public class PDialog {
    private ProgressDialog pDialog;

    public PDialog(Context c){
        pDialog = new ProgressDialog(c);
        pDialog.setCancelable(false);
    }

    public void set(String txt){
        pDialog.setMessage(txt);
    }

    public void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
