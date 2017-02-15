package com.asce4s.gogreen.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Event;
import com.asce4s.gogreen.util.Location;
import com.asce4s.gogreen.util.Votes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class Events extends Fragment implements OnMapReadyCallback {


    private GoogleMap mMap;
    private DatabaseReference mVotes;
    private DatabaseReference mLikes;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
   //private static View rootView;
    public Events() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       /*if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_add_post, container, false);
        } catch (Exception ex) {
            Log.d("pop", "onCreateView: " + ex.getMessage());
        }*/

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mVotes = FirebaseDatabase.getInstance().getReference().child("events");
        mLikes = FirebaseDatabase.getInstance().getReference().child("votes");


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }


            @Override
            public View getInfoContents(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.mapinfo, null);

                TextView title = (TextView) v.findViewById(R.id.tv_title);
                TextView date = (TextView) v.findViewById(R.id.tv_date);
                TextView loc = (TextView) v.findViewById(R.id.tvv_location);

                Event p = (Event) marker.getTag();

                title.setText(marker.getTitle());
                date.setText(p.getDate() + " at " + p.getTime());
                loc.setText(p.getPlace().getName());

                return v;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Bundle b=new Bundle();
                EventSingle e=new EventSingle();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                b.putString("eventID",marker.getSnippet());
                e.setArguments(b);
                ft.addToBackStack(UUID.randomUUID().toString());
                ft.replace(R.id.frame_container, e);
                ft.commit();
            }
        });


        mVotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Event e = dsp.getValue(Event.class);
                    LatLng l = new LatLng(e.getPlace().getLatitude(), e.getPlace().getLongtitude());
                    mMap.addMarker(new MarkerOptions().position(l).title(e.getTitle()).snippet(dsp.getKey())).setTag(e);



//                    mLikes.addValueEventListener(new ValueEventListener() {
//
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            int count = 0;
//                            for (DataSnapshot sd:dataSnapshot.getChildren()) {
//                                if(e.getPlace() == sd.getValue(Votes.class).getL()) {
//                                    count++;
//                                }
//                            };
//
//                            mMap.addMarker(new MarkerOptions().position(l).title("Count: " + count)).setTag(e);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(l)
                            .radius(10000)
                            .strokeColor(Color.RED)
                            .strokeWidth(5)
                            .fillColor(Color.TRANSPARENT));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        LatLng Sl = new LatLng(7.759798, 80.675546);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Sl));
    }
}
