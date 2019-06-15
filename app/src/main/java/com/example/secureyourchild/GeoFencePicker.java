package com.example.secureyourchild;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoFencePicker extends FragmentActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    public static final int MY_PERMISSTION_REQUESR_CODE=1996;
    public static final int PLAY_SERVICE_RESOLUTION_REQUEST=890;
    private GoogleApiClient mgoogleApiClient;
    private Location lastlocation;
    private LocationRequest mlocationRequest;
    private LocationCallback locationCallback;
    private int UPDATE_INTERVAL=5000;
    private int FASTEST_INTERVAL=3000;
    private int Displacement=10;
    Marker currentmarker;
    DatabaseReference mdatabaseRef;
    LatLng latLngtoconvert ;
    Geocoder geocoder;
    String add = null;
    String city = null;
    String state= null;
    String country= null;
    String postalCode= null;
    String knownName= null;
    StationInfo stationInfo;
    Marker marker;
    FirebaseUser firebaseUser,user_data;
    FirebaseAuth firebaseAuth,mAuth;
    String child_location_refrence="All_Child_Location_GeoFire",UID,CHILDID;


    SharedPreferences sharedPreferences,sharedPreferences1;
    SharedPreferences.Editor sharedEditor,sharedEditor1;
    private String latval="latval",lonval="lonval",latkey="latkey",lonkey="lonkey",cid="cid";
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences=getSharedPreferences(latval,MODE_PRIVATE);
        sharedEditor=sharedPreferences.edit();
        sharedPreferences1=getSharedPreferences(lonval,MODE_PRIVATE);
        sharedEditor1=sharedPreferences1.edit();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        UID=firebaseUser.getUid();
        mdatabaseRef= FirebaseDatabase.getInstance().getReference("All_GeoFences").child(UID);
        setUpLocation();
    }














    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSTION_REQUESR_CODE:
                if (grantResults.length<0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if (checkPlayservice()){
                        builtGoogleApiCliect();
                        createLocatinoRequest();
                        DisplayLocation();
                    }
                }
                break;
        }
    }
    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //Request runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSTION_REQUESR_CODE);
        }
        else{
            if (checkPlayservice()){
                builtGoogleApiCliect();
                createLocatinoRequest();
                DisplayLocation();
            }
        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
//        locationCallback=new LocationCallback();
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient,mlocationRequest,this);
//        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest,locationCallback,null);
    }
    private void createLocatinoRequest() {
        mlocationRequest=new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(Displacement);
    }
    private void builtGoogleApiCliect() {
        mgoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
    }
    private boolean checkPlayservice() {
        int resultcode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode!= ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)){
                GooglePlayServicesUtil.getErrorDialog(resultcode,this,PLAY_SERVICE_RESOLUTION_REQUEST).show();
            }
            else {
                Toast.makeText(this, "This Device Is Not Supported 😭", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }
    private void DisplayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        lastlocation= LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (lastlocation!=null){
            final double latitude=lastlocation.getLatitude();
            final double longitude=lastlocation.getLongitude();
            if (currentmarker!=null){
                currentmarker.remove();
                //currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Your Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

            }
            else {
                //currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));

            }
            Log.d("EDMTDEV",String.format("Your location was changed : %f / %f ",latitude,longitude));

        }
        else {
            Log.d("EDMTDEV","Can not get Location");
        }

    }
    private String ConvertlatlongToPlace(LatLng latLng){
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses=new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (addresses.size()>0){
//            Address address=list.get(0);
//            Log.d(TAG,"geoLocate : found a location : "+address.toString());
//            moveCameraView(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

            add = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        }
        String uploadId=mdatabaseRef.push().getKey();
        stationInfo=new StationInfo(add,city,state,country,postalCode,latLng.latitude,latLng.longitude,uploadId);
        return add+" "+city+" "+state+" "+country+" "+postalCode+" - "+latLng.latitude+" - "+latLng.longitude+uploadId;
    }













    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(23.7545861, 90.3752816);
        currentmarker=mMap.addMarker(new MarkerOptions().position(sydney).title("Daffodil International University"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.7545861, 90.3752816), 12.0f));
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        latLngtoconvert = mMap.getCameraPosition().target;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DisplayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation=location;
        DisplayLocation();
    }

    public void AddLocationBtn(View view) {
        latLngtoconvert = mMap.getCameraPosition().target;
        ConvertlatlongToPlace((latLngtoconvert));
        //Toast.makeText(this, stationInfo.add+stationInfo.uploadId, Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder alert=new AlertDialog.Builder(GeoFencePicker.this);
        alert.setTitle("Confirm..");
        alert.setMessage("Do You Want to Save a new GeoFence at --("+stationInfo.getAdd()+")-- Press Yes to save");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(GeoFencePicker.this, "Yes", Toast.LENGTH_SHORT).show();
//                UID=firebaseUser.getUid();
                sharedEditor.putString(latkey, String.valueOf(stationInfo.getLat()));
                sharedEditor.putString(lonkey, String.valueOf(stationInfo.getLon()));
//                sharedEditor1.putString(lonkey, String.valueOf(stationInfo.getLon()));
                sharedEditor.apply();
//                sharedEditor1.apply();

//                Toast.makeText(GeoFencePicker.this, sharedPreferences.getString(latkey,"0"), Toast.LENGTH_SHORT).show();
//                Toast.makeText(GeoFencePicker.this, sharedPreferences.getString(lonkey,"0"), Toast.LENGTH_SHORT).show();

                mdatabaseRef.child(UID).setValue(stationInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GeoFencePicker.this, "Successfully Station Saved at "+stationInfo.getAdd(), Toast.LENGTH_SHORT).show();
                        Intent mainint=new Intent(GeoFencePicker.this,ParentMapActivity.class);
                        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainint);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GeoFencePicker.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GeoFencePicker.this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();

    }
}
