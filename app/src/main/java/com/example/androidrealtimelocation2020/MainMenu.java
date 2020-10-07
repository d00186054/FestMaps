package com.example.androidrealtimelocation2020;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidrealtimelocation2020.Interface.IFirebaseLoadDone;
import com.example.androidrealtimelocation2020.Interface.IRecyclerItemClickListener;
import com.example.androidrealtimelocation2020.Model.User;
import com.example.androidrealtimelocation2020.Service.MyLocationReceiver;
import com.example.androidrealtimelocation2020.Utils.Common;
import com.example.androidrealtimelocation2020.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter,searchAdapter;
    RecyclerView recycler_friend_list;
    IFirebaseLoadDone firebaseLoadDone;
    TextView fragmentTitle;



    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    private ActionBarDrawerToggle t;


    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = findViewById(R.id.nav_view);

        ImageButton profileImageButton = findViewById(R.id.toolbar_profile);
        ImageButton searchImageButton = findViewById(R.id.toolbar_search);
        ImageButton addFriendImageButton = findViewById(R.id.toolbar_add_friend);
        ImageButton settingImageButton = findViewById(R.id.toolbar_settings);
        fragmentTitle = findViewById(R.id.toolbar_title);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();



        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainMenu.this, "Profile",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainMenu.this, DisplayFriendLocation.class));
            }
        });
        searchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainMenu.this, "Search",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainMenu.this, AllPeopleActivity.class);
                startActivity(intent);
            }
        });
        addFriendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainMenu.this, "Add Friend",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainMenu.this, FriendRequestActivity.class);
                startActivity(intent);
            }
        });
        settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainMenu.this, "Settings",Toast.LENGTH_SHORT).show();
            }
        });




        updateLocation();

        firebaseLoadDone = this;


    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_map:
                            selectedFragment = new MapFragment();
                            fragmentTitle.setText("Map");
                            break;
                        case R.id.nav_stage_line_up:
                            selectedFragment = new LineupFragment();
                            fragmentTitle.setText("Line Up");
                            break;
                        case R.id.nav_weather:
                            selectedFragment = new WeatherFragment();
                            fragmentTitle.setText("Weather");
                            break;
                        case R.id.nav_news:
                            selectedFragment = new NewsFragment();
                            fragmentTitle.setText("News");
                            break;
                        case R.id.nav_info:
                            selectedFragment = new InfoFragment();
                            fragmentTitle.setText("Info");
                            break;
                    }

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };




    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(MainMenu.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }





    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
