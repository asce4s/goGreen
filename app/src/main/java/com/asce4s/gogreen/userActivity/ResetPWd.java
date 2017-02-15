package com.asce4s.gogreen.userActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.FormValidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPWd extends Activity {

    private Button btn_reset;
    private EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        btn_reset = (Button) findViewById(R.id.btn_reset);
        et_email = (EditText) findViewById(R.id.et_email);
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please wait");
        pDialog.setCancelable(false);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FormValidate vv = new FormValidate();
                if (vv.validateReq(et_email) && vv.validateEmail(et_email)) {
                    pDialog.show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(et_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        pDialog.setTitleText("Check your email");
                                        pDialog.setConfirmText("Ok");
                                        et_email.setText("");
                                    }
                                }
                            });
                }
            }
        });
    }

}
