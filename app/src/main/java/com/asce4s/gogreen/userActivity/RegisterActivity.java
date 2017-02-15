package com.asce4s.gogreen.userActivity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.FormValidate;
import com.asce4s.gogreen.util.PDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name, et_email, et_password, et_cpassword;
    private Button btn_join;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_pw);
        et_cpassword = (EditText) findViewById(R.id.et_cpw);
        btn_join = (Button) findViewById(R.id.btn_reg);

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }


    void register() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please wait");
        pDialog.setCancelable(false);


        String email = et_email.getText().toString();
        String password = et_password.getText().toString();


        FormValidate validate = new FormValidate();
        EditText[] etArray = {et_name, et_email, et_password, et_cpassword};
        if (validate.validateReq(etArray) &&
                validate.validateEmail(et_email) &&
                validate.match(et_password, et_cpassword, "Passwords doesn't match")&&
                validate.charCount(et_password,6)
                ) {
            pDialog.show();


            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("User already exists");
                        pDialog.setConfirmText("Ok");

                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                pDialog.dismissWithAnimation();
                            }
                        });

                    } else {
                        FirebaseUser user=auth.getCurrentUser();
                        if (user != null) {
                            String name=et_name.getText().toString();

                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                pDialog.setTitleText("Account Created");
                                                pDialog.setConfirmText("Login");

                                            }
                                        }
                                    });
                        } else {
                            // No user is signed in
                        }



                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }
                        });



                    }
                }
            });
        }

    }
}
