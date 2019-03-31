package com.example.user.hairr;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMap hospitalLocation;
    double myLat;
    double myLng;
    LatLng myLatLng;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker mCurrLocationMarker2;
    double theirLatitude;
    double theirLongitude;
    String name, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        myLat =Double.parseDouble( getIntent().getStringExtra("myLat"));
        myLng = Double.parseDouble(getIntent().getStringExtra("myLng"));
        theirLatitude = Double.parseDouble(getIntent().getStringExtra("stylistLat"));
        theirLongitude = Double.parseDouble(getIntent().getStringExtra("stylistLng"));
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            finish();
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
    }

    private void getLocation() {

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        hospitalLocation = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


        myLatLng = new LatLng(myLat,myLng);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        mMap.clear();



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLng);
        markerOptions.title("My current location");

        // Adding colour to the marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        // Adding Marker to the Map
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoomLevel));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


        if (mCurrLocationMarker2 != null) {
            mCurrLocationMarker2.remove();
        }

        MarkerOptions markerOptions2 = new MarkerOptions();
        LatLng latLng = new LatLng(theirLatitude, theirLongitude);
        markerOptions2.title(name);
        // Position of Marker on Map
        markerOptions2.position(latLng);
        // Adding Title to the Marker
        // Adding Marker to the Camera.
        markerOptions2.snippet("Stylist current location: "+address);
        mCurrLocationMarker2 = mMap.addMarker(markerOptions2);
        // Adding colour to the marker
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


        // Creating CameraUpdate object for zoom
        float zoomLevel2 = 17.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel2));
        hospitalLocation.animateCamera(CameraUpdateFactory.zoomTo(11));

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", myLat, myLng));

        Log.d("onLocationChanged", "Exit");


    }
}
