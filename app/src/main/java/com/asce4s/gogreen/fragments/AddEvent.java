package com.asce4s.gogreen.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.CalenderView;
import com.asce4s.gogreen.util.Event;
import com.asce4s.gogreen.util.FormValidate;
import com.asce4s.gogreen.util.Location;
import com.asce4s.gogreen.util.Post;
import com.asce4s.gogreen.util.TimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AddEvent extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int GALLERY_REQ = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Button btn_save;
    private EditText et_title, et_desc,et_date,et_time;
    private ImageView iv_img;
    private Uri imgURI = null;
    private StorageReference storageRef;
    private DatabaseReference mDbRef;
    private Location loc = null;
    static View rootView;

    public AddEvent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Event");


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
        } catch (Exception ex) {
            Log.d("pop", "onCreateView: " + ex.getMessage());
        }



        return rootView;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_title = (EditText) rootView.findViewById(R.id.et_title);
        et_desc = (EditText) rootView.findViewById(R.id.et_desc);
        et_date = (EditText) rootView.findViewById(R.id.et_date);
        et_time = (EditText) rootView.findViewById(R.id.et_time);

        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        iv_img = (ImageView) rootView.findViewById(R.id.iv_img);

        btn_save.setOnClickListener(this);
        iv_img.setOnClickListener(this);
        et_date.setOnClickListener(this);
        et_time.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Location");
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(5.603369, 79.603084),
                new LatLng(10.172941, 82.275450)));


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("dgdf", "Place: " + place.toString());

                loc = new Location(place.getName().toString(), place.getAddress().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dsgfd", "An error occurred: " + status);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        //rootView=null;


    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        //rootView=null;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.iv_img:
                changeImage();
                break;
            case R.id.et_time:
                TimePicker t=new TimePicker();
                t.setEt(et_time);
                t.show(getActivity().getFragmentManager(), "Pick Time");
                break;
            case R.id.et_date:
                new CalenderView(getActivity(), et_date).show();


        }
    }

    private void save() {
        final SweetAlertDialog pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pd.setTitleText("Adding..");
        pd.setCancelable(false);
        FormValidate v = new FormValidate();

        if (v.validateReq(new EditText[]{et_title, et_desc}) && loc != null) {
            pd.show();
            storageRef = FirebaseStorage.getInstance().getReference().child("event_img").child(user.getUid());
            StorageReference filepath = storageRef.child(imgURI.getLastPathSegment());
            filepath.putFile(imgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String img = taskSnapshot.getDownloadUrl().toString();

                    mDbRef = FirebaseDatabase.getInstance().getReference().child("events");


                    Event p = new Event(et_title.getText().toString(),
                            img,
                            et_desc.getText().toString(),
                            user.getUid(),
                            user.getDisplayName(),
                            loc,
                            et_date.getText().toString(),
                            et_time.getText().toString());

                    mDbRef.push().setValue(p);

                    pd.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pd.setTitleText("Event added");
                    pd.setConfirmText("Ok");

                    iv_img.setImageResource(R.drawable.placeholder);
                    et_title.setText("");
                    et_desc.setText("");
                    autocompleteFragment.setText("");
                    et_date.setText("");
                    et_time.setText("");
                }
            });


        }


    }

    private void changeImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {

            Uri imgUri = data.getData();
            CropImage.activity(imgUri)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imgURI = result.getUri();
                iv_img.setImageURI(imgURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }


    @Override
    public void onDestroyView() {
        // rootView=null;

        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment));
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }


    @Override
    public void onDetach() {
        //rootView=null;
        super.onDetach();
    }
}
