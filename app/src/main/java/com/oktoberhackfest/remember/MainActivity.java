package com.oktoberhackfest.remember;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.oktoberhackfest.remember.realmobjects.RealmPlace;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private DrawerLayout mDrawerLayout;

    int PLACE_PICKER_REQUEST = 1;
    private PlacePicker.IntentBuilder builder;
    private MaterialMenuIconCompat materialMenu;
    private boolean isDrawerOpened;
    private boolean googleApiReady = false;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Realm realm;
    private NearPlaceFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        materialMenu = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawerLayout.openDrawer(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.dr_menu_remember:
                        if (googleApiReady) {
                            try {
                                startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    case R.id.dr_menu_list:
                        // update the main content by replacing fragments
                        fragment = new NearPlaceFragment();

                        FragmentManager fragmentManager = getSupportFragmentManager(); // For AppCompat use getSupportFragmentManager
                        fragmentManager.beginTransaction()
                                .replace(R.id.drawer_content, fragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });


        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                materialMenu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpened ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if(newState == DrawerLayout.STATE_IDLE) {
                    if(isDrawerOpened) {
                        materialMenu.setState(MaterialMenuDrawable.IconState.ARROW);
                    } else {
                        materialMenu.setState(MaterialMenuDrawable.IconState.BURGER);
                    }
                }
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        builder = new PlacePicker.IntentBuilder();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isDrawerOpened = mDrawerLayout.isDrawerOpen(GravityCompat.START); // or END, LEFT, RIGHT
        materialMenu.syncState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        materialMenu.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(navigationView)) {
                mDrawerLayout.closeDrawers();
            } else {
                mDrawerLayout.openDrawer(navigationView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();



                //commit to db
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmPlace dbPlace = realm.createObject(RealmPlace.class);
                        dbPlace.setName(place.getName().toString());
                        dbPlace.setAddress(place.getAddress().toString());
                        //refresh view
                        if (fragment != null) {
                            fragment.getNearPlaceListAdapter().add(new NearPlaceListItem(place.getName().toString(),
                                    place.getAddress().toString()));
                            fragment.getNearPlaceListAdapter().notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        googleApiReady = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void setTitle(CharSequence title) {
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }
}
