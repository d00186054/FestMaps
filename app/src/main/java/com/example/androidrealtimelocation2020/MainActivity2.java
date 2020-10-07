package com.example.androidrealtimelocation2020;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, InfoWindowManager.WindowShowListener {

    FusedLocationProviderClient client;
    // MapFragment mapFragment;
    Button setTentLocation;
    GoogleMap map;
    Marker tent;
    String currentCustomMarker = "random";
    LatLng currentLocation;
    private static final String TAG = "MainActivity";
    private ArrayList<Marker> mMarkersList = new ArrayList<>();
    private Marker myMarker;

    private static final String RECYCLER_VIEW = "RECYCLER_VIEW_MARKER";
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";

    private InfoWindow recyclerWindow;
    private InfoWindow formWindow;
    private InfoWindowManager infoWindowManager;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTentLocation = findViewById(R.id.setTentLocation);




        final MapInfoWindowFragment mapInfoWindowFragment =
                (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.mapNearBy);

        infoWindowManager = mapInfoWindowFragment.infoWindowManager();
        infoWindowManager.setHideOnFling(true);


        client = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


        }else{
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }


        FloatingActionButton fab_action_toilet = findViewById(R.id.fab_action_toilet);
        FloatingActionButton fab_action_stage = findViewById(R.id.fab_action_stage);
        FloatingActionButton fab_action_water = findViewById(R.id.fab_action_water);
        FloatingActionButton fab_action_food = findViewById(R.id.fab_action_food);

        fab_action_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "toilet";
            }
        });

        fab_action_stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "stage";
            }
        });

        fab_action_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "water";
            }
        });

        fab_action_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "food";
            }
        });


        mapInfoWindowFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                final Marker marker1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(5, 5)).snippet(RECYCLER_VIEW));
                final Marker marker2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(1, 1)).snippet(FORM_VIEW));

                final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);

                final InfoWindow.MarkerSpecification markerSpec =
                        new InfoWindow.MarkerSpecification(offsetX, offsetY);

                recyclerWindow = new InfoWindow(marker1, markerSpec, new RecyclerViewFragment());
                formWindow = new InfoWindow(marker2, markerSpec, new FormFragment());

                googleMap.setOnMarkerClickListener(MainActivity2.this);
            }
        });

        infoWindowManager.setWindowShowListener(MainActivity2.this);




    }



    private void removeTripMarkers(){
        for(Marker marker: mMarkersList){
            marker.remove();
        }
    }






    private void setTentLocation() {

        setTentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<Location> temp = client.getLastLocation();
                temp.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null)
                        {
                            if(tent != null){
                                tent.remove();
                            }
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            setTentLocation(currentLocation);
                            Toast.makeText(MainActivity2.this, "Tent Location Updated", Toast.LENGTH_SHORT).show();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                            tent = map.addMarker(setTentLocation(currentLocation));
                        }

                    }
                });


            }
        });


    }


    public MarkerOptions setCustomMarker(LatLng latLng, String customMarker){
        int height = 100;
        int width = 100;
        Bitmap b = null;

        if(customMarker == "toilet"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.toilet2);
        }else if(customMarker == "stage"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.stage);
        }else if(customMarker == "water"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        } else if(customMarker == "food"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.food);
        } else {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        }

        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        MarkerOptions option = new MarkerOptions().position(latLng).title(customMarker).icon(smallMarkerIcon);

        return option;
    }





    public MarkerOptions setTentLocation(LatLng latLng){
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.iconcamping);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        MarkerOptions tent = new MarkerOptions().position(latLng).title("This is my tent").icon(smallMarkerIcon);

        return tent;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this));
//        map.setMyLocationEnabled(true);
//
//        setTentLocation();
//        map.setOnMapLongClickListener(this);
//        map.setOnMarkerDragListener(this);
//        ZoomMapOnLocation();



//
//        final Marker marker1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(5, 5)).snippet(RECYCLER_VIEW));
//        final Marker marker2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(1, 1)).snippet(FORM_VIEW));
//
//        final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
//        final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
//
//        final InfoWindow.MarkerSpecification markerSpec =
//                new InfoWindow.MarkerSpecification(offsetX, offsetY);
//
//        recyclerWindow = new InfoWindow(marker1, markerSpec, new RecyclerViewFragment());
//        formWindow = new InfoWindow(marker2, markerSpec, new FormFragment());
//
//        googleMap.setOnMarkerClickListener(MainActivity.this);





    }


    public void ZoomMapOnLocation(){

        Task<Location> temp = client.getLastLocation();
        temp.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());

        Marker markerLongClick = map.addMarker(setCustomMarker(latLng, currentCustomMarker).draggable(true));
        map.setOnMarkerClickListener(this);
        mMarkersList.add(markerLongClick);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "OnMarkerDragStart: " );
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "OnMarkerDrag: " );
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "OnMarkerDragEnd: " );
        LatLng latLng = marker.getPosition();
        marker.setTitle(latLng.toString());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


//        for(Marker marker2: mMarkersList){
//            if(marker.equals(marker2)){
//                marker2.remove();
//            }
//
//        }
//
//        if(marker.equals(myMarker))
//        {
//            Toast.makeText(this, "I selected a marker", Toast.LENGTH_SHORT).show();
//        }
//        return false;


        InfoWindow infoWindow = null;
        switch (marker.getSnippet()) {
            case RECYCLER_VIEW:
                infoWindow = recyclerWindow;
                break;
            case FORM_VIEW:
                infoWindow = formWindow;
                break;
        }

        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }

        return true;
    }

    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {

    }
}
