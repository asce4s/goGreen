package com.asce4s.gogreen.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Post;
import com.asce4s.gogreen.util.Votes;
import com.daimajia.androidanimations.library.sliders.SlideInRightAnimator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.UUID;


public class Wall extends Fragment implements View.OnClickListener{

    private FloatingActionButton btn_addPost;
    private DatabaseReference mDbRef;
    private RecyclerView rv;
    private FirebaseUser user;
    private FirebaseAuth mauth;
    private boolean liked=false;
    private DatabaseReference mLike;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView= inflater.inflate(R.layout.fragment_wall, container, false);
        mauth = FirebaseAuth.getInstance();

        user = mauth.getCurrentUser();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        btn_addPost=(FloatingActionButton)rootView.findViewById(R.id.btn_addPost);
        btn_addPost.setOnClickListener(this);
        mDbRef= FirebaseDatabase.getInstance().getReference().child("posts");
        mDbRef.keepSynced(true);

        mLike=FirebaseDatabase.getInstance().getReference().child("votes");
        mLike.keepSynced(true);
        rv=(RecyclerView)rootView.findViewById(R.id.rv);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setHasFixedSize(true);
        llm.setStackFromEnd(true);
        rv.setLayoutManager(llm);

        FirebaseRecyclerAdapter<Post,PostHolder> ad=new FirebaseRecyclerAdapter<Post, PostHolder>(
                Post.class,
                R.layout.card_single_post,
                PostHolder.class,
                mDbRef
        ) {
            @Override
            protected void populateViewHolder(PostHolder viewHolder, final Post model, int position) {
                final String postID=getRef(position).getKey();
                viewHolder.name.setText(model.getTitle());
                Picasso.with(getContext()).load(model.getImageURI()).into(viewHolder.img);
                viewHolder.location.setText("In "+model.getPlace().getName()+" by "+model.getDisplay_name());
                viewHolder.desc.setText(model.getDescription());

                viewHolder.discuss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Discuss d=new Discuss();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        bundle.putString("postID",postID);
                        d.setArguments(bundle);
                        ft.addToBackStack(UUID.randomUUID().toString());
                        ft.replace(R.id.frame_container, d);
                        ft.commit();
                    }
                });
               viewHolder.setLike(postID,user.getUid());

                viewHolder.vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        liked=true;


                            mLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(liked) {
                                        if (dataSnapshot.child(postID).hasChild(user.getUid())) {
                                            mLike.child(postID).child(user.getUid()).removeValue();
                                            liked = false;
                                        } else {
                                            Votes v=new Votes(model.getPlace(),user.getUid());
                                            mLike.child(postID).child(user.getUid()).setValue(v);
                                           // mLike.child(postID).child("location").setValue(model.getPlace());
                                            liked = false;
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            //m.child(user.getUid()).setValue(user.getDisplayName());
                        }

                });


            }
        };

        rv.setAdapter(ad);
        rv.smoothScrollToPosition(0);



        return rootView;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addPost:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(UUID.randomUUID().toString());
                ft.replace(R.id.frame_container, new AddPost());
                ft.commit();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        View mView;
        CardView cv;
        TextView name,location,desc,discuss,vote;
        ImageView img;

        public PostHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cv = (CardView)itemView.findViewById(R.id.cv);
            name = (TextView)itemView.findViewById(R.id.tv_card_name);
            location = (TextView)itemView.findViewById(R.id.tv_card_location);
            desc = (TextView)itemView.findViewById(R.id.tv_card_desc);
            img = (ImageView)itemView.findViewById(R.id.iv_card_img);
            discuss=(TextView)itemView.findViewById(R.id.tv_discuss);
            vote=(TextView)itemView.findViewById(R.id.btn_card_like);
        }

        public void setLike(final String postID, final String uid){
            final DatabaseReference mLike=FirebaseDatabase.getInstance().getReference().child("votes");
            mLike.keepSynced(true);

            mLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postID).hasChild(uid)){
                       vote.setTextColor(Color.rgb(41,147,84));

                    }else{

                        vote.setTextColor(Color.DKGRAY);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView=null;


    }


}
