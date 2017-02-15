package com.asce4s.gogreen.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Event;
import com.asce4s.gogreen.util.Participate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventSingle extends Fragment {

    private String eventID = null;
    private DatabaseReference mEventData;
    private DatabaseReference mEventPrt;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ImageView iv_img;
    private TextView tv_title, tv_desc, tv_name,tv_date,tv_count;
    private ToggleButton tb_check;
    private boolean clicked=true;
    private int count=0;

    public EventSingle() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Event");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_event_single, container, false);
        iv_img = (ImageView) rootView.findViewById(R.id.iv_img);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_date= (TextView) rootView.findViewById(R.id.tv_date);
        tv_count= (TextView) rootView.findViewById(R.id.tv_count);
        tb_check= (ToggleButton) rootView.findViewById(R.id.tb_check);




        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        eventID = mBundle.getString("eventID");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mEventData = FirebaseDatabase.getInstance().getReference().child("events").child(eventID);
        mEventPrt = FirebaseDatabase.getInstance().getReference().child("participants").child(eventID);
        mEventData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event e=dataSnapshot.getValue(Event.class);
                Picasso.with(getContext()).load(e.getImageURI()).into(iv_img);
                tv_title.setText(e.getTitle());
                tv_name.setText("In " + e.getPlace().getName() + " by " + e.getDisplay_name());
                tv_desc.setText(e.getDescription());
                tv_date.setText(e.getDate() + " at " + e.getTime());
                count=e.getCount();
                tv_count.setText(count+"");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEventPrt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUid())) {
                    if (clicked) {
                        tb_check.setChecked(true);
                        clicked=false;

                    } else {
                        tb_check.setChecked(false);
                        clicked=false;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(!false) {
                   if (isChecked) {
                       count++;
                       mEventData.child("count").setValue(count);
                       mEventPrt.child(user.getUid()).setValue(user.getDisplayName());

                   } else {
                       count--;
                       mEventData.child("count").setValue(count);
                       mEventPrt.child(user.getUid()).removeValue();
                   }
               }
            }
        });

        return rootView;
    }



}
