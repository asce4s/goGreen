package com.asce4s.gogreen.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.asce4s.gogreen.R;
import com.asce4s.gogreen.util.Comment;
import com.asce4s.gogreen.util.FormValidate;
import com.asce4s.gogreen.util.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
public class Discuss extends Fragment {

    private DatabaseReference mComment;
    private DatabaseReference mPostData;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String postID = null;
    private ImageButton btn_add;
    private  EditText et_cmnt;

    private ImageView iv_img;
    private TextView tv_title, tv_desc, tv_name;
    private RecyclerView rv_view;
    View rootView;
    public Discuss() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Discuss");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discuss, container, false);
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        postID = mBundle.getString("postID");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mComment = FirebaseDatabase.getInstance().getReference().child("comments");
        mPostData = FirebaseDatabase.getInstance().getReference().child("posts").child(postID);


        iv_img = (ImageView) rootView.findViewById(R.id.iv_img);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        btn_add = (ImageButton) rootView.findViewById(R.id.btn_add);
        et_cmnt = (EditText) rootView.findViewById(R.id.et_comment);


        rv_view=(RecyclerView)rootView.findViewById(R.id.rv_view) ;
        LinearLayoutManager llm = new LinearLayoutManager(getContext());

        //rv_view.setHasFixedSize(true);
        //llm.setReverseLayout(true);
        rv_view.setLayoutManager(llm);



        mPostData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);
                Picasso.with(getContext()).load(p.getImageURI()).into(iv_img);
                tv_title.setText(p.getTitle());
                tv_name.setText("In " + p.getPlace().getName() + " by " + p.getDisplay_name());
                tv_desc.setText(p.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Comment,CommentHolder> rad=new FirebaseRecyclerAdapter<Comment,CommentHolder>(
                Comment.class,
                R.layout.comment_single,
                CommentHolder.class,
                mComment.child(postID)
        ) {
            @Override
            protected void populateViewHolder(CommentHolder v, Comment model, int position) {
                v.name.setText(model.getName());
                v.cmnt.setText(model.getCmnt());

            }

            @Override
            protected void onCancelled(DatabaseError databaseError) {
                super.onCancelled(databaseError);

            }
        };


        rv_view.setAdapter(rad);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new FormValidate().validateReq(et_cmnt)){

                    Comment c=new Comment(user.getUid(),user.getDisplayName(),et_cmnt.getText().toString());
                    mComment.child(postID).push().setValue(c);
                    rv_view.smoothScrollToPosition(rv_view.getAdapter().getItemCount());
                    et_cmnt.setText("");
                }
            }
        });




        return rootView;
    }


    public static class CommentHolder extends RecyclerView.ViewHolder {

        TextView name,cmnt;
        public CommentHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.tv_name);
            cmnt=(TextView)itemView.findViewById(R.id.tv_cmnt);

        }


    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onPause() {
        super.onPause();
        rootView=null;
        rv_view.setAdapter(null);
    }

    @Override
    public void onDestroyView() {
        rootView=null;
        rv_view.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        rootView=null;
        rv_view.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        rootView=null;
        rv_view.setAdapter(null);
        super.onDetach();
    }
}
