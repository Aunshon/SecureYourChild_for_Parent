package com.example.secureyourchild;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class ParentMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener{

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
    private DatabaseReference addchildDatabase,cInfo;
    Marker currentmarker, childLocationMarker;
    DatabaseReference ref;
    GeoFire geoFire;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String child_location_refrence="All_Child_Location_GeoFire",UID,CHILDID,user_type="user_type";
    private String latval="latval",lonval="lonval",latkey="latkey",lonkey="lonkey";
    FirebaseUser firebaseUser,user_data;
    FirebaseAuth firebaseAuth,mAuth;
    SharedPreferences.Editor sharedEditor,sharedEditor1;
    SharedPreferences sharedPreferences,sharedPreferences1;

    NotificationCompat.Builder notiifcation;
    public static final int uniqueId=1111;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private View view;
    NavigationView navigationView;
    TextView user_name,user_phone;
    DatabaseReference mdatabaseRef;

    double latitude,longitude;
    Geocoder geocoder;
    LatLng latLngtoconvert ;
    String add = null;
    String city = null;
    String state= null;
    String country= null;
    String postalCode= null;
    String knownName= null;

    StationInfo stationInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_map);

        drawerLayout=findViewById(R.id.drawer);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView=findViewById(R.id.nav_view);
        view=navigationView.getHeaderView(0);
        user_name=view.findViewById(R.id.user_name);
        user_phone=view.findViewById(R.id.user_phone);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        UID=firebaseUser.getUid();
        mdatabaseRef= FirebaseDatabase.getInstance().getReference("All_GeoFences").child(UID);
        //////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();
        user_data=mAuth.getCurrentUser();

        String Name;
        cInfo=FirebaseDatabase.getInstance().getReference("Users");
        cInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    UserInfoClass userData=postSnapshot.getValue(UserInfoClass.class);
                    if (user_data.getUid().equals(userData.getUid())){
                        user_name.setText(userData.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        user_phone.setText(user_data.getPhoneNumber());

/////////////////////////////////////////////////////////////////
        notiifcation = new NotificationCompat.Builder(this);
        notiifcation.setAutoCancel(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences=getSharedPreferences(latval,MODE_PRIVATE);
        sharedPreferences1=getSharedPreferences(user_type,MODE_PRIVATE);
        sharedEditor=sharedPreferences.edit();
        sharedEditor1=sharedPreferences1.edit();
//        sharedPreferences1=getSharedPreferences(lonval,MODE_PRIVATE);


        latitude=Double.parseDouble(sharedPreferences.getString(latkey,"0"));
        longitude=Double.parseDouble(sharedPreferences.getString(lonkey,"0"));
//        Toast.makeText(this, sharedPreferences.getString(latkey,"0"), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, sharedPreferences.getString(lonkey,"0"), Toast.LENGTH_SHORT).show();
        Log.d("lat", String.valueOf(latitude));
        Log.d("lon", String.valueOf(longitude));

        sharedEditor=sharedPreferences.edit();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference(child_location_refrence);
        geoFire=new GeoFire(ref);
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
                        mMap.clear();
                        DisplayLocation();
                    }
                }
                break;
        }
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
        if (Double.parseDouble(sharedPreferences.getString(latkey,"0")) != 0 && Double.parseDouble(sharedPreferences.getString(lonkey,"0"))!=0){
            LatLng dengerusArea=new LatLng(Double.parseDouble(sharedPreferences.getString(latkey,"0")),Double.parseDouble(sharedPreferences.getString(lonkey,"0")));
            mMap.addCircle(new CircleOptions()
                    .center(dengerusArea)
                    .radius(500)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f));
            //mMap.addMarker(new MarkerOptions().position(dengerusArea).title("Geofence"));
            GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(dengerusArea.latitude,dengerusArea.longitude),0.5f);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(final String key, final GeoLocation location) {
//                Toast.makeText(ParentMapActivity.this, "key--"+ key, Toast.LENGTH_SHORT).show();









                    UID=firebaseUser.getUid();
                    cInfo=FirebaseDatabase.getInstance().getReference("Users");
                    cInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                                UserInfoClass userData=postSnapshot.getValue(UserInfoClass.class);
                                if (key.equals(userData.getUid())){
//                                Toast.makeText(ParentMapActivity.this, userData.getUsername(), Toast.LENGTH_SHORT).show();

                                    notiifcation.setSmallIcon(R.drawable.ic_launcher_background);
                                    notiifcation.setTicker("ticker");
                                    notiifcation.setContentTitle("Child Entered in Geofence");
                                    latLngtoconvert=new LatLng(location.latitude,location.longitude);
                                    notiifcation.setContentText(userData.getUsername()+" is now  in "+ConvertlatlongToPlace(latLngtoconvert)+" area");
                                    notiifcation.setWhen(System.currentTimeMillis());
                                    notiifcation.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                                    Intent intent=new Intent(ParentMapActivity.this,ParentMapActivity.class);
                                    PendingIntent pendingIntent= (PendingIntent) PendingIntent.getActivities(ParentMapActivity.this,0, new Intent[]{intent},PendingIntent.FLAG_UPDATE_CURRENT);

                                    notiifcation.setContentIntent(pendingIntent);

                                    NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.notify(uniqueId,notiifcation.build());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });












                    mMap.clear();
                    DisplayLocation();
                    CreateGeofince();
//                if (childLocationMarker!=null){
//                    childLocationMarker.remove();
//                    mMap.clear();
//                }
                }

                @Override
                public void onKeyExited(final String key) {
//                Toast.makeText(ParentMapActivity.this, "Exited", Toast.LENGTH_SHORT).show();










                    UID=firebaseUser.getUid();
                    cInfo=FirebaseDatabase.getInstance().getReference("Users");
                    cInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                                UserInfoClass userData=postSnapshot.getValue(UserInfoClass.class);
                                if (key.equals(userData.getUid())){
//                                Toast.makeText(ParentMapActivity.this, userData.getUsername(), Toast.LENGTH_SHORT).show();

                                    notiifcation.setSmallIcon(R.drawable.ic_launcher_background);
                                    notiifcation.setTicker("ticker");
                                    notiifcation.setContentTitle("Exited");
                                    notiifcation.setContentText(userData.getUsername()+" is Out of the area");
                                    notiifcation.setWhen(System.currentTimeMillis());
                                    notiifcation.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                                    Intent intent=new Intent(ParentMapActivity.this,ParentMapActivity.class);
                                    PendingIntent pendingIntent= (PendingIntent) PendingIntent.getActivities(ParentMapActivity.this,0, new Intent[]{intent},PendingIntent.FLAG_UPDATE_CURRENT);

                                    notiifcation.setContentIntent(pendingIntent);

                                    NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.notify(uniqueId,notiifcation.build());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });














                    mMap.clear();
                    DisplayLocation();
                    CreateGeofince();
//                if (childLocationMarker!=null){
//                    childLocationMarker.remove();
//                    mMap.clear();
//                }

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
//                Toast.makeText(ParentMapActivity.this, "Moving Inside Area", Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    DisplayLocation();
                    CreateGeofince();
                }

                @Override
                public void onGeoQueryReady() {
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.parent_map_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
            case R.id.map_ChildAdd_byParent:
                //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //Toast.makeText(this, "Child added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ParentMapActivity.this,AddChildActivityFromParent.class));
                return true;
            case R.id.mychild:
                startActivity(new Intent(ParentMapActivity.this,AllChild.class));
                return true;
            case R.id.GeoFences:
                startActivity(new Intent(ParentMapActivity.this,GeoFencePicker.class));
                return true;
            case R.id.signout:

                final AlertDialog.Builder alert=new AlertDialog.Builder(this);
                alert.setTitle("Confirm...");
                alert.setMessage("Are You Sure , You Want To SignOut 😭 ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        sharedEditor.clear();
                        sharedEditor.apply();

                        sharedEditor1.clear();
                        sharedEditor1.apply();

                        Intent mainint = new Intent(ParentMapActivity.this, MainActivity.class);
                        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainint);
                    }
                });
                alert.setCancelable(true);
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ParentMapActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mMap.clear();
        DisplayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mgoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation=location;
        mMap.clear();
        DisplayLocation();
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
                currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Your Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12.0f));
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

                setChildLocation();
                CreateGeofince();
                CreateGeofince();
            }
            else {
                currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12.0f));

            }
            Log.d("EDMTDEV",String.format("Your location was changed : %f / %f ",latitude,longitude));

        }
        else {
            Log.d("EDMTDEV","Can not get Location");
        }

    }

    private void setChildLocation() {
        if (firebaseUser!=null){
            UID=firebaseUser.getUid();
            addchildDatabase=FirebaseDatabase.getInstance().getReference().child("added_child_by_parent").child(UID);
            addchildDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (childLocationMarker!=null){
                        childLocationMarker.remove();
                    }
                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                        final ChildAddClass childAddClass=postSnapshot.getValue(ChildAddClass.class);
                        //Toast.makeText(ParentMapActivity.this, childAddClass.getChildID()+" "+childAddClass.getChildName(), Toast.LENGTH_SHORT).show();
                        geoFire.getLocation(childAddClass.getChildID(), new com.firebase.geofire.LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation location) {
                                if (location!=null){
                                    //Toast.makeText(ParentMapActivity.this, "lat "+location.latitude+" long "+location.longitude, Toast.LENGTH_SHORT).show();
                                    LatLng s = new LatLng(location.latitude, location.longitude);
                                    childLocationMarker =mMap.addMarker(new MarkerOptions().position(s).title(childAddClass.getChildName()));
                                    //childLocationMarker =mMap.addMarker(new MarkerOptions().position(s).title(location.toString()));
                                }
                                else {
                                    Toast.makeText(ParentMapActivity.this, "There is no location for key "+key, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void CreateGeofince() {
//        mdatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
//                    StationInfo userData=postSnapshot.getValue(StationInfo.class);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(ParentMapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        if (Double.parseDouble(sharedPreferences.getString(latkey,"0")) != 0 && Double.parseDouble(sharedPreferences.getString(lonkey,"0"))!=0){
            LatLng dengerusArea=new LatLng(Double.parseDouble(sharedPreferences.getString(latkey,"0")),Double.parseDouble(sharedPreferences.getString(lonkey,"0")));
            mMap.addCircle(new CircleOptions()
                    .center(dengerusArea)
                    .radius(500)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f));
            //mMap.addMarker(new MarkerOptions().position(dengerusArea).title("Geofence"));
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
//        stationInfo=new StationInfo(add,city,state,country,postalCode,latLng.latitude,latLng.longitude,uploadId);
//        return add+" "+city+" "+state+" "+country+" "+postalCode+" - "+latLng.latitude+" - "+latLng.longitude+uploadId;
        return add;
    }

}
