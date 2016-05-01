package com.smartagencysm.threepoints.activity;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.smartagencysm.threepoints.R;
import com.smartagencysm.threepoints.fragment.PathInfoFragment;
import com.smartagencysm.threepoints.rest.RestClient;
import com.smartagencysm.threepoints.rest.ResultResponse;
import com.smartagencysm.threepoints.utils.MapCounting;
import com.smartagencysm.threepoints.utils.NetworkUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

@EActivity(R.layout.main_layout)
public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private Marker[] markerPoints;
    private Marker myLocationMarker;
    private Polyline line;

    private int pointsCounter;

    @ViewById
    Toolbar toolbar;

    @RestService
    RestClient api;

    private PathInfoFragment pathInfo;

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            addMyLocationMarker(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(MainActivity.this, "Network: " + status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                addMyLocationMarker(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @AfterViews
    void ready() {
        markerPoints = new Marker[3];
        setSupportActionBar(toolbar);
        setTitle("");
        createMapView();
        putMarkerData();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkUtils.isNetworkAvailable(this)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 50, locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 50, locationListener);
        } else {
            Toast.makeText(this, "ON Network or GPS to get your location",Toast.LENGTH_SHORT).show();
        }

        addLocationMarker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void putMarkerData() {
        markerPoints[0] = map.addMarker(new MarkerOptions()
                .position(new LatLng(50.51161641335251, 30.6028263643384))
                );

        markerPoints[1] = map.addMarker(new MarkerOptions()
                .position(new LatLng(50.38356680098725, 30.476762205362316))
               );

        markerPoints[2] = map.addMarker(new MarkerOptions()
                .position(new LatLng(50.434330085505465, 30.522050298750404))
               );

        pointsCounter = 3;
    }

    private void addMyLocationMarker(Location location){

        if(null != map){
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

            if (myLocationMarker != null) {
                myLocationMarker.remove();
            }

            myLocationMarker = map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
            );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(15)
                    .build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.animateCamera(update);
            addPath();
        }
    }

    private void addLocationMarker() {
        if (map != null) {
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    if (pointsCounter < markerPoints.length) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                        .position(latLng)
                        );

                        markerPoints[pointsCounter++] = marker;
                    } else {
                        Toast.makeText(MainActivity.this, "Three points are exists here", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pointsCounter > markerPoints.length) {
                        pointsCounter = markerPoints.length;
                    }

                    if (myLocationMarker != null) {
                        addPath();
                    }

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Error creating map", Toast.LENGTH_SHORT).show();
        }

    }

    public void removeLocationMarkers(MenuItem menuItem) {

        for (int i = 0; i < markerPoints.length; i++) {
            if (markerPoints[i] != null) {
                markerPoints[i].remove();
                markerPoints[i] = null;
            }
        }

        if (line != null) {
            line.remove();
        }

        pointsCounter = 0;

        if (getSupportFragmentManager().findFragmentByTag(PathInfoFragment.TAG) != null) {
            getSupportFragmentManager().beginTransaction().remove(pathInfo).commit();
        }
    }

    private void addPath() {
        int index = MapCounting.minIndex(myLocationMarker, markerPoints);
        if (index > -1) {
            Log.i("MIN", "Min index: " + index);
            addPath(myLocationMarker, markerPoints[index]);
        } else {
            Toast.makeText(this, "Map not include transit points!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addPath(MenuItem menuItem) {
        if (myLocationMarker != null) {
            addPath();
        } else {
            Toast.makeText(this, "Can't show you location on the map", Toast.LENGTH_SHORT).show();
        }
    }

     private void addPath(Marker myLocation, Marker otherPointLocation) {

        LatLng myLatLng = myLocation.getPosition();
        LatLng otherLatLng = otherPointLocation.getPosition();

        String origin = myLatLng.latitude + "," + myLatLng.longitude;
        String destination = otherLatLng.latitude + "," + otherLatLng.longitude;

        if (NetworkUtils.isNetworkAvailable(this)) {
            getPath(origin, destination);
        } else {
            Toast.makeText(this, "Network disable", Toast.LENGTH_SHORT).show();
        }

    }

    @Background
    void getPath(String origin, String destination) {
        ResultResponse result = api.getPath(origin, destination, true, "ru");

        String points = result.getPoint();
        String from = result.getStartAddress();
        String to = result.getEndAddress();
        String distance = result.getDistance();
        String duration = result.getDuration();

        Log.i("MIN", points);
        drawLine(points, from, to, distance, duration);
    }

    @UiThread
    void drawLine(String points, String from, String to, String distance, String duration) {
        List<LatLng> mPoints = PolyUtil.decode(points);
        PolylineOptions lineOptions = new PolylineOptions().width(8f).color(R.color.colorPrimary);
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < mPoints.size(); i++) {
            lineOptions.add(mPoints.get(i));
            latLngBuilder.include(mPoints.get(i));
        }

        if (line != null) {
            line.remove();
        }

        line = map.addPolyline(lineOptions);
        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);

        pathInfo = PathInfoFragment.getInstance(from, to, distance, duration);

        if (getSupportFragmentManager().findFragmentByTag(PathInfoFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,
                    pathInfo, PathInfoFragment.TAG).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    pathInfo, PathInfoFragment.TAG).commit();
        }

    }

    private void createMapView(){

        try {
            if(null == map){
                map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                if(null == map) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map",Toast.LENGTH_SHORT).show();
                }


            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

}
