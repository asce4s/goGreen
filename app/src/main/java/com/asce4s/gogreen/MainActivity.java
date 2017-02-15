package com.asce4s.gogreen;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.asce4s.gogreen.fragments.AddEvent;
import com.asce4s.gogreen.fragments.AddPost;
import com.asce4s.gogreen.fragments.Events;
import com.asce4s.gogreen.fragments.MyEvents;
import com.asce4s.gogreen.fragments.MyPost;
import com.asce4s.gogreen.fragments.ProfileEdit;
import com.asce4s.gogreen.fragments.Wall;
import com.asce4s.gogreen.userActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private ProfileDrawerItem profile;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

       setSupportActionBar(toolbar);
       getSupportActionBar().setTitle(R.string.app_name);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });


        Uri display= Uri.parse("android.resource://com.asce4s.gogreen/drawable/default_male");
        
        if (currentUser.getPhotoUrl()!=null){
            display=currentUser.getPhotoUrl();
        }


        
        profile = new ProfileDrawerItem().withName(currentUser.getDisplayName()).withIcon(display);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                )
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Wall").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new ExpandableDrawerItem().withName("Events").withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(2).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("Events").withLevel(2).withIcon(FontAwesome.Icon.faw_map).withIdentifier(2000),
                                //new SecondaryDrawerItem().withName("My Events").withLevel(2).withIcon(FontAwesome.Icon.faw_street_view).withIdentifier(2001),
                                new SecondaryDrawerItem().withName("Add Event").withLevel(2).withIcon(FontAwesome.Icon.faw_plus).withIdentifier(2002)
                        ),

                        new ExpandableDrawerItem().withName("Posts").withIcon(FontAwesome.Icon.faw_folder).withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("My posts").withLevel(2).withIcon(FontAwesome.Icon.faw_folder_open).withIdentifier(3001),
                                new SecondaryDrawerItem().withName("Add post").withLevel(2).withIcon(FontAwesome.Icon.faw_pencil).withIdentifier(3002)
                        ),

                        //new PrimaryDrawerItem().withName("My Posts").withIcon(FontAwesome.Icon.faw_flag).withIdentifier(3),
                       // new PrimaryDrawerItem().withName("Notifications").withIcon(FontAwesome.Icon.faw_flag).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Profile").withIcon(FontAwesome.Icon.faw_user).withIdentifier(5)

                ) // add the items we want to use with our Drawer
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                        //if the back arrow is shown. close the activity
                        MainActivity.this.finish();
                        //return true if we have consumed the event
                        return true;
                    }
                })
                .addStickyDrawerItems(
                       // new SecondaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(10),
                        new SecondaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_power_off).withIdentifier(11)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        if (drawerItem != null) {
                            Intent intent = null;

                            if (drawerItem.getIdentifier() == 1) {
                                changeLayout(new Wall(), "Wall");
                            }
                            if (drawerItem.getIdentifier() == 2002) {
                                changeLayout(new AddEvent(), "Add Events");
                            }
                            if (drawerItem.getIdentifier() == 2000) {
                                changeLayout(new Events(), "Events");
                            }

                            if (drawerItem.getIdentifier() == 2001) {
                                changeLayout(new MyEvents(), "My Events");
                            }

                            if (drawerItem.getIdentifier() == 3001) {
                                changeLayout(new MyPost(), "My posts");
                            }
                            if (drawerItem.getIdentifier() == 3002) {
                                changeLayout(new AddPost(), "Add post");
                            }

                            if (drawerItem.getIdentifier() == 5) {
                                changeLayout(new ProfileEdit(), "Profile");
                            }

                            if (drawerItem.getIdentifier() == 11) {
                                mAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();

                            }


                        } else {
                            changeLayout(new Wall(), "Wall");
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        changeLayout(new Wall(), "Wall");


    }

    private void changeLayout(Fragment fr, String title) {

        getSupportActionBar().setTitle(title);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fr);
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        /*switch (item.getItemId()) {
            case R.id.menu_1:
                //update the profile2 and set a new image.
                profile2.withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_android).backgroundColorRes(R.color.accent).sizeDp(48).paddingDp(4));
                headerResult.updateProfileByIdentifier(profile2);
                return true;
            case R.id.menu_2:
                //show the arrow icon
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                return true;
            case R.id.menu_3:
                //show the hamburger icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                return true;
            case R.id.menu_4:
                //we want to replace our current header with a compact header
                //build the new compact header
                buildHeader(true, null);
                //set the view to the result
                result.setHeader(headerResult.getView());
                //set the drawer to the header (so it will manage the profile list correctly)
                headerResult.setDrawer(result);
                return true;
            case R.id.menu_5:
                //we want to replace our current header with a normal header
                //build the new compact header
                buildHeader(false, null);
                //set the view to the result
                result.setHeader(headerResult.getView());
                //set the drawer to the header (so it will manage the profile list correctly)
                headerResult.setDrawer(result);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(result!=null && headerResult!=null) {
            //add the values which need to be saved from the drawer to the bundle
            outState = result.saveInstanceState(outState);
            //add the values which need to be saved from the accountHeader to the bundle
            outState = headerResult.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
