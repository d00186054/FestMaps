package com.example.androidrealtimelocation2020;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, InfoWindowManager.WindowShowListener {

    FusedLocationProviderClient client;
    Button setTentLocation;
    GoogleMap map;
    Marker tent;
    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;
    String currentCustomMarker = "tent";
    LatLng currentLocation;
    FloatingActionsMenu fab_menu;
    Drawable myIcon;
    LocationRequest locationRequest;
    private static final String TAG = "MainActivity";
    private ArrayList<Marker> mMarkersList = new ArrayList<>();
    private Marker myMarker;
    private FragmentActivity myContext;


    private static final String RECYCLER_VIEW = "RECYCLER_VIEW_MARKER";
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";
    private static final String CUSTOM_VIEW = "CUSTOM_VIEW_MARKER";

    private InfoWindow recyclerWindow;
    private InfoWindow formWindow;
    private InfoWindow customWindow;
    private InfoWindowManager infoWindowManager;


    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private Button infoButtonEdit;
    MapWrapperLayout mapWrapperLayout;
    private OnInfoWindowElemTouchListener infoButtonListener;




    private static View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            Log.d("testing","View is not null");
            if (parent != null){}
        }
        try {
            Log.d("testing","tryinggg");
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        Log.d("testing","just before setTentLocation");

        setTentLocation = view.findViewById(R.id.setTentLocation);






        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapNearBy);
        mapWrapperLayout = view.findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);



        client = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){


        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        fab_menu = view.findViewById(R.id.floating_menu);
        toan.android.floatingactionmenu.FloatingActionButton fab_action_toilet = view.findViewById(R.id.fab_action_toilet);
        toan.android.floatingactionmenu.FloatingActionButton fab_action_stage = view.findViewById(R.id.fab_action_stage);
        toan.android.floatingactionmenu.FloatingActionButton fab_action_water = view.findViewById(R.id.fab_action_water);
        toan.android.floatingactionmenu.FloatingActionButton fab_action_food = view.findViewById(R.id.fab_action_food);
        toan.android.floatingactionmenu.FloatingActionButton fab_action_tent = view.findViewById(R.id.fab_action_tent);

        fab_action_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "toilet";
                myIcon = getResources().getDrawable( R.drawable.toilet2 );
                fab_menu.setIcon(myIcon);
                fab_menu.collapse();

            }
        });

        fab_action_stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "stage";
                myIcon = getResources().getDrawable( R.drawable.stage );
                fab_menu.setIcon(myIcon);
                fab_menu.collapse();
            }
        });

        fab_action_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "water";
                myIcon = getResources().getDrawable( R.drawable.water );
                fab_menu.setIcon(myIcon);
                fab_menu.collapse();
            }
        });

        fab_action_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "food";
                myIcon = getResources().getDrawable( R.drawable.food );
                fab_menu.setIcon(myIcon);
                fab_menu.collapse();
            }
        });

        fab_action_tent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCustomMarker = "tent";
                myIcon = getResources().getDrawable( R.drawable.iconcamping );
                fab_menu.setIcon(myIcon);
                fab_menu.collapse();
            }
        });
        return view;
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
                                //tent.remove();
                            }
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            setIconCurrentLocation(currentLocation);
                            Toast.makeText(getActivity(), "Tent Location Updated", Toast.LENGTH_SHORT).show();
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                            tent = map.addMarker(setIconCurrentLocation(currentLocation));
                            mMarkersList.add(tent);
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
        } else if(customMarker == "tent"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.iconcamping);
        } else {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        }

        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        MarkerOptions option = new MarkerOptions().position(latLng).title(customMarker).icon(smallMarkerIcon).snippet(FORM_VIEW);

        return option;
    }


    LocationCallback locationCallback = new LocationCallback(){
      @Override
      public void onLocationResult(LocationResult locationResult){
          super.onLocationResult(locationResult);
          Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
          if(map != null){
              setUserLocationMarker(locationResult.getLastLocation());
          }
      }
    };

    private void setUserLocationMarker(Location location){

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        int height = 120;
        int width = 120;
        Bitmap b;
        b = BitmapFactory.decodeResource(getResources(), R.drawable.dora);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        if(userLocationMarker == null){
            //create new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(smallMarkerIcon);
            //markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = map.addMarker(markerOptions);
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            //user previously created marker
            userLocationMarker.setPosition(latLng);
            //userLocationMarker.setRotation(location.getBearing());
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        if(userLocationAccuracyCircle == null){
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = map.addCircle(circleOptions);
        } else{
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }


    private void startLocationUpdates(){
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates(){
        client.removeLocationUpdates(locationCallback);
    }




    public MarkerOptions setIconCurrentLocation(LatLng latLng){
        int height = 100;
        int width = 100;

        Bitmap b = null;

        if(currentCustomMarker == "toilet"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.toilet2);
        }else if(currentCustomMarker == "stage"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.stage);
        }else if(currentCustomMarker == "water"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        } else if(currentCustomMarker == "food"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.food);
        } else if(currentCustomMarker == "tent"){
            b = BitmapFactory.decodeResource(getResources(), R.drawable.iconcamping);
        } else {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        }
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        MarkerOptions tent = new MarkerOptions().position(latLng).title("This is my tent").icon(smallMarkerIcon).snippet(FORM_VIEW);

        return tent;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //map.setMyLocationEnabled(true);
        setTentLocation();
        map.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);
        map.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) this);
        ZoomMapOnLocation();

        mapWrapperLayout.init(map, getPixelsFromDp(getActivity(), 39 + 20));

        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);



        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.round_button),
                getResources().getDrawable(R.drawable.round_button))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                marker.remove();
                Toast.makeText(getActivity(), marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };

        this.infoButton.setOnTouchListener(infoButtonListener);



        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);


                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });


        googleMap.setOnMarkerClickListener(MapFragment.this);

    }


    public void ZoomMapOnLocation(){

        Task<Location> temp = client.getLastLocation();
        temp.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));

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



    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        Marker markerLongClick = map.addMarker(setCustomMarker(latLng, currentCustomMarker).draggable(true));
        map.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        return false;
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }else{

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
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
