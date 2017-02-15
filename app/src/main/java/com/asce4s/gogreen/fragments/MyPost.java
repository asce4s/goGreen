package com.asce4s.gogreen.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Post;
import com.asce4s.gogreen.util.Votes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MyPost extends Fragment {

    private Query mDbRef;
    private FirebaseUser user;
    private FirebaseAuth mauth;
    private RecyclerView rv;
    private  View rootView;
    public MyPost() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_my_post, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mauth = FirebaseAuth.getInstance();
        rv=(RecyclerView)  rootView.findViewById(R.id.rv);


        user = mauth.getCurrentUser();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);

        mDbRef= FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("uid").equalTo(user.getUid());

        FirebaseRecyclerAdapter<Post,PostHolder> ad=new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.comment_single,
                PostHolder.class,
                mDbRef
        ) {
            @Override
            protected void populateViewHolder(PostHolder viewHolder, Post model, int position) {
                final String postID=getRef(position).getKey();
                viewHolder.tv1.setText(model.getTitle());
                viewHolder.tv2.setText(model.getPlace().getName());
                viewHolder.setPostID(postID);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        PostEdit d=new PostEdit() ;
                        FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                        bundle.putString("postID",postID);
                        d.setArguments(bundle);
                        ft.addToBackStack(UUID.randomUUID().toString());
                        ft.replace(R.id.frame_container, d);
                        ft.commit();
                    }
                });



            }
        };

        rv.setAdapter(ad);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public static class PostHolder extends RecyclerView.ViewHolder {

        TextView tv1,tv2;

       String postID=null;
        View mView;

        public String getPostID() {
            return postID;
        }

        public void setPostID(String postID) {
            this.postID = postID;
        }

        public PostHolder(final View itemView) {
            super(itemView);
            tv1=(TextView)itemView.findViewById(R.id.tv_name);
            tv2=(TextView)itemView.findViewById(R.id.tv_cmnt);
            mView=itemView;


        }



    }









}
