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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.asce4s.gogreen.MainActivity;
import com.asce4s.gogreen.R;
import com.asce4s.gogreen.userActivity.LoginActivity;
import com.asce4s.gogreen.util.FormValidate;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfileEdit extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private static final int GALLERY_REQ = 1;

    private ImageView iv_profile;
    private TextView tv_name;
    private EditText et_name, et_email,et_pw,et_cpw;
    private Button btn_save, btn_changepw, btn_fb, btn_google;
    private Uri profileIMG = null;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageRef;
    private boolean picChanged = false;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private LoginManager fbLoginManager;
    private SweetAlertDialog pDialog;
    private CallbackManager callbackManager;
    private AuthCredential credential = null;

    public ProfileEdit() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        pDialog = new SweetAlertDialog(getContext());
        pDialog.setCancelable(false);

        iv_profile = (ImageView) rootView.findViewById(R.id.iv_profile);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);

        et_name = (EditText) rootView.findViewById(R.id.et_name);
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        et_pw = (EditText) rootView.findViewById(R.id.et_pw);
        et_cpw = (EditText) rootView.findViewById(R.id.et_cpw);

        btn_fb = (Button) rootView.findViewById(R.id.btn_fb);
        btn_google = (Button) rootView.findViewById(R.id.btn_google);
        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_changepw = (Button) rootView.findViewById(R.id.btn_changepw);

        btn_fb.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_changepw.setOnClickListener(this);
        iv_profile.setOnClickListener(this);

        profileIMG = user.getPhotoUrl();
        tv_name.setText(user.getDisplayName());
        et_name.setText(user.getDisplayName());
        et_email.setText(user.getEmail());

        List<String> authProviders = user.getProviders();

        if (authProviders.contains("google.com")) {
            btn_google.setText("Unlink");
        }

        if (profileIMG != null) {
            Picasso.with(iv_profile.getContext()).load(profileIMG).into(iv_profile);
        }

        fbLoginManager = LoginManager.getInstance();


        fbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fbbbbb", "facebook:onSuccess:" + loginResult);
                credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("fblink", "linkWithCredential:onComplete:" + task.isSuccessful());

                                if (!task.isSuccessful()) {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText("Linking failed");
                                    pDialog.setConfirmText("Try again");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                        }
                                    });

                                }

                            }
                        });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("fbbbbb", "facebook:onerror:" + error);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        return rootView;


    }


    private void changeImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQ);
    }

    private void save() {


        FormValidate v = new FormValidate();

        final SweetAlertDialog pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pd.setTitleText("Updating..");
        pd.setCancelable(false);
        if (v.validateReq(new EditText[]{et_name, et_email}) &&
                v.validateEmail(et_email)
                ) {
            pd.show();


            if (picChanged) {
                storageRef = FirebaseStorage.getInstance().getReference().child("profile_img").child(user.getUid());
                StorageReference filepath = storageRef.child(profileIMG.getLastPathSegment());
                filepath.putFile(profileIMG).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        updateData(taskSnapshot.getDownloadUrl().toString());
                        pd.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pd.setTitleText("Profile Updated");
                        pd.setConfirmText("Ok");
                    }
                });
            } else {
                updateData(String.valueOf(user.getPhotoUrl()));
                pd.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                pd.setTitleText("Profile Updated");
                pd.setConfirmText("Ok");

            }

            pd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    pd.dismissWithAnimation();
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            });


        }

    }

    public void updateData(String url) {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(url))
                .build();

        user.updateProfile(profileUpdates);
        user.updateEmail(email);

    }

    private void googleAuth() {
        pDialog.dismiss();
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Linking google..");
        pDialog.setCancelable(false);
        pDialog.setConfirmText(null);
        pDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("rrrr", "facebook:onSuccess:" + requestCode);
        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {

            Uri imgUri = data.getData();
            CropImage.activity(imgUri)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                profileIMG = result.getUri();
                iv_profile.setImageURI(profileIMG);
                picChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d("userxxxxxx", result.toString());
                credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());

                                if (!task.isSuccessful()) {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText("Linking failed");
                                    pDialog.setConfirmText("Try again");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            googleAuth();
                                        }
                                    });

                                } else {
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText("Google Linked");
                                    pDialog.setConfirmText("Ok");
                                  //btn_google.setText("Unlink");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            reload();
                                        }
                                    });

                                }

                            }
                        });
            } else {
                Log.d("userxxxxxx", result.isSuccess() + "");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:
                changeImage();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_changepw:
                changepw();
                break;
            case R.id.btn_google:
                if (btn_google.getText().toString().equals("Unlink")) {
                    unlinkGoogle();
                } else
                    googleAuth();
                break;
            case R.id.btn_fb:
                fbLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                break;


        }

    }

    private void unlinkGoogle() {
        pDialog.dismiss();
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Unlinking google..");
        pDialog.setCancelable(false);
        pDialog.show();
        mAuth.getCurrentUser().unlink("google.com")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("Unlinking failed");
                            pDialog.setConfirmText("Try again");
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    unlinkGoogle();
                                }
                            });

                        }else{
                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            pDialog.setTitleText("Google Unlinked");
                            pDialog.setConfirmText("Ok");
                           // btn_google.setText("Google");
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    reload();
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void reload(){
        pDialog.dismiss();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void changepw(){
        FormValidate v=new FormValidate();
        if (v.validateReq(new EditText[]{et_pw, et_cpw})&&
                v.charCount(et_pw,6)&&
                v.match(et_pw,et_cpw,"Passwords doesn't match")
                ){
            pDialog.dismiss();
            pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Setting new password");
            pDialog.setCancelable(false);
            pDialog.show();
            user.updatePassword(et_pw.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("Password Updated");
                                pDialog.setConfirmText("Ok");
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        pDialog.dismissWithAnimation();
                                    }
                                });
                            }else{
                                Log.d("asaA",task.getException().getMessage());
                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText("Failed");
                                pDialog.setContentText("Please login again before changing your password");
                                pDialog.setConfirmText("Logout");
                                pDialog.showCancelButton(true);
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        mAuth.signOut();
                                        startActivity(new Intent(getContext(), LoginActivity.class));

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("xcxcx",e.getMessage());
                }
            });

        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }


}
