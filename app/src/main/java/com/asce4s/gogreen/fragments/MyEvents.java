package com.asce4s.gogreen.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Event;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEvents extends Fragment {


    private RecyclerView rv;
    private DatabaseReference mDb;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    public MyEvents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_my_events, container, false);
        rv=(RecyclerView)rootView.findViewById(R.id.rv);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance().getReference().child("events");

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv=(RecyclerView)rootView.findViewById(R.id.rv);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setHasFixedSize(true);
        llm.setStackFromEnd(true);
        rv.setLayoutManager(llm);


        FirebaseRecyclerAdapter<Event,EventHolder> ad=new FirebaseRecyclerAdapter<Event, EventHolder>(
                Event.class,
                android.R.layout.two_line_list_item,
                EventHolder.class,
                mDb
        ) {
            @Override
            protected void populateViewHolder(EventHolder viewHolder, Event model, int position) {
                viewHolder.tv_t1.setText(model.getTitle());

            }
        };

        return rootView;
    }

    public static class EventHolder extends RecyclerView.ViewHolder {

        TextView tv_t1;
        public EventHolder(View itemView) {
            super(itemView);
           // tv_t1=(TextView)itemView.findViewById(R.id.text1);
        }
    }

}
