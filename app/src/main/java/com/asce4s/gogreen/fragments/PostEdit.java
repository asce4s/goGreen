package com.asce4s.gogreen.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.FormValidate;
import com.asce4s.gogreen.util.Location;
import com.asce4s.gogreen.util.Post;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostEdit extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private static final int GALLERY_REQ = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Button btn_save,btn_del;
    private EditText et_title, et_desc;
    private ImageView iv_img;
    private Uri imgURI = null;
    private StorageReference storageRef;
    private DatabaseReference mDbRef;
    private Location loc = null;
    private static View rootView;
    private android.app.FragmentManager fm;
    private DatabaseReference mPostData;
    private String postID = null,img;
    private Location lx;

    public PostEdit() {
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Post");
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        postID = mBundle.getString("postID");

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
            rootView = inflater.inflate(R.layout.fragment_edit_post, container, false);
        } catch (Exception ex) {
            Log.d("pop", "onCreateView: " + ex.getMessage());
        }
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_title = (EditText) rootView.findViewById(R.id.et_title);
        et_desc = (EditText) rootView.findViewById(R.id.et_desc);
        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_del = (Button) rootView.findViewById(R.id.btn_del);
        iv_img = (ImageView) rootView.findViewById(R.id.iv_img);

        btn_save.setText("Save");
        btn_save.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        iv_img.setOnClickListener(this);
        mPostData = FirebaseDatabase.getInstance().getReference().child("posts").child(postID);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fm= getActivity().getFragmentManager();

        autocompleteFragment = (PlaceAutocompleteFragment)
                fm.findFragmentById(R.id.place_autocomplete_fragment);

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

        mPostData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Post p = dataSnapshot.getValue(Post.class);
                    img = p.getImageURI();
                    lx = p.getPlace();
                    Picasso.with(getContext()).load(p.getImageURI()).into(iv_img);
                    et_title.setText(p.getTitle());
                    et_desc.setText(p.getDescription());
                    autocompleteFragment.setText(p.getPlace().getName());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    private void save() {
        final SweetAlertDialog pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pd.setTitleText("Posting..");
        pd.setCancelable(false);
        FormValidate v = new FormValidate();

        if (v.validateReq(new EditText[]{et_title, et_desc}) ) {
            pd.show();
            storageRef = FirebaseStorage.getInstance().getReference().child("post_img").child(user.getUid());
            StorageReference filepath;
            if(x) {
                filepath = storageRef.child(imgURI.getLastPathSegment());
                filepath.putFile(imgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String imgx = taskSnapshot.getDownloadUrl().toString();
                        img=imgx;


                    }
                });
            }
            mDbRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postID);
            Post p = new Post(et_title.getText().toString(),
                    img,
                    et_desc.getText().toString(),
                    user.getUid(),
                    user.getDisplayName(),
                    lx);

            mDbRef.setValue(p);

            pd.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            pd.setTitleText("Saved");
            pd.setConfirmText("Ok");





        }


    }

    private void changeImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQ);
    }

    private boolean x=false;
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
                x=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_del:
                del();
                break;
            case R.id.iv_img:
                changeImage();
        }
    }

    private void del() {
        final SweetAlertDialog pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pd.setTitleText("Posting..");
        pd.setCancelable(false);
        pd.show();
        mDbRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postID);
        mDbRef.removeValue();
        pd.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        pd.setTitleText("Posted");
        pd.setConfirmText("Ok");
        pd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, new Wall());
                ft.commit();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        fm.beginTransaction().remove(autocompleteFragment).commit();


    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        //rootView=null;


    }

}
