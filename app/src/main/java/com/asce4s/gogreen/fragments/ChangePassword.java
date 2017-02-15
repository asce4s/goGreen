package com.asce4s.gogreen.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asce4s.gogreen.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassword extends Activity {


    public ChangePassword() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_password_reset);
    }
}
