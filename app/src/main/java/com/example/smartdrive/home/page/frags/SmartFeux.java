package com.example.smartdrive.home.page.frags;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.smartdrive.R;
import com.example.smartdrive.SigninSignup;
import com.example.smartdrive.SplashScreen;
import com.example.smartdrive.UserScreen;
import com.example.smartdrive.Utility.ProgressBar;
import com.example.smartdrive.VipScreen;
import com.example.smartdrive.home.page.frags.Interface.IOnLoadLocationListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

//to open the settings
import android.provider.Settings;




public class SmartFeux extends Fragment implements OnMapReadyCallback, GeoQueryEventListener, IOnLoadLocationListener {


    // *** buttons ***

    //Nearby Zone button
    ConstraintLayout nearbyZone_btn;


    // *** buttons ***



    //check to not redondante the notification
    private boolean userEnterToTheZone = false;



    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    //database reference
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    //smartFeux zones
    private List<LatLng> smartFeuxZones;
    //LoadListener interface
    private IOnLoadLocationListener listener;
    //mahdia child
    private DatabaseReference mahdia;

    //to set the geofire again (user mark)
    private Location lastLocation;
    private GeoQuery geoQuery;








    //  *** other variables   ***

    //progress bar
    ProgressBar progressBar;


    //enable gps settings request code
    private static final int REQUEST_CHECK_SETTINGS = 123;

    //is vip in the zone ref
    DatabaseReference isVipInZone ;

    //route  database and fstore reference
    DatabaseReference laneNumber ;

    //reference to firestore to get the user access
    FirebaseFirestore fStore;










    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smart_feux, container, false);


        //nearby zone hook
        nearbyZone_btn = v.findViewById(R.id.nearbyZone_btn);


        //initialise the progressbar
        progressBar = new ProgressBar(getContext());

        //initialise the firebase elements
        isVipInZone = FirebaseDatabase.getInstance().getReference();
        //initialise the firebase elements
        laneNumber = FirebaseDatabase.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();


        //initialise the isVipInZone State (first time set it to false)
        isVipInZone.child("isVipInZone").setValue(false);


        //test if the gps is open or not either request to enable it
        if(!isLocationProviderEnabled(requireContext())){
            gpsEnableDialog();
        }else{

            progressBar.show();

        }



        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        //obtained the supportMapFragment and get notified when the map is ready to be used

                        buildLocationRequest();
                        buildLocationCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

                        initArea();
                        settingGeofire();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        snackBar("vous devez activez l'autorisation", 2000, R.color.baseRed, R.drawable.ic_error);


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();




        nearbyZone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNearestZone();

            }
        });




        return v;





    }  //end onCreate













    private void initArea() {


        mahdia = FirebaseDatabase.getInstance()
                .getReference("FeuxZones")
                .child("Mahdia");


        listener = this;

        //load zones from databse
        mahdia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //update smart feux zones list
                List<MyLatLng> latLngList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {

                    MyLatLng latLng = locationSnapshot.getValue(MyLatLng.class);
                    latLngList.add(latLng);
                }



                listener.onLoadLocationSuccess(latLngList);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // => we use this to set our reference on the BD just for the first time
  /*      FirebaseDatabase.getInstance()
                //mon reference sur RTDB
                .getReference("FeuxZones")
                .child("Mahdia")
                //pass the array list to the firebase
                .setValue(smartFeuxZones)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "erreur : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        */




    }

    private void addUserMarker() {

        //setting our geofire
        geoFire.setLocation("Toi", new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

                if (currentUser != null) currentUser.remove();
                currentUser = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                        .title("Toi"));

                //after add marker, move camera
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(currentUser.getPosition(), 12.0f));

            }
        });
    }

    private void settingGeofire() {

        myLocationRef = FirebaseDatabase.getInstance().getReference("MonLocation");
        geoFire = new GeoFire(myLocationRef);


    }
    private void buildLocationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull final LocationResult locationResult) {
                if (mMap != null) {


                    lastLocation = locationResult.getLastLocation();
                    addUserMarker();


                }
            }
        };


    }
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        progressBar.dismiss();

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (fusedLocationProviderClient != null) {

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


            //add circle for smart feux zones
            addCircleArea();

        }



    }

    private void addCircleArea() {

        if(geoQuery != null){

            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();

        }

        for(LatLng latLng : smartFeuxZones){

            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(500) //500m distance zone
                    .strokeColor(getResources().getColor(R.color.green_settings))
                    .fillColor(0x2200FF00)  //22 is transparent color
                    .strokeWidth(5.0f)
            );

            //Create GeoQuery when user in a smart feux zone
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),0.5f);  //500m
            geoQuery.addGeoQueryEventListener(SmartFeux.this);

        }
    }





    @Override
    public void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {


        if (!isVisible()) {
            return; // Fragment is hidden, exit the method
        }


        //user is now on the feux zone (this variable is for set the moving of user just one time)
        userEnterToTheZone = true;


        //check for no pointer exception
        if(isAdded() && getContext() != null){
            sendNotification("Entered",String.format("%s Entrée dans la zone des feux",key));
        }

        Toast.makeText(getContext(), "entered the smart feux zone", Toast.LENGTH_SHORT).show();

        //methode called when the vip result is available
        isUserVip(getUserUid(), new VipCallback() {
            @Override
            public void onVipReceived(boolean isVip) {
                // Do something with the boolean value
                if(isVip && getEmergencyState()){


                    isVipInZone.child("isVipInZone").setValue(true);



                   openInformationFragment();




                }else{

                    isVipInZone.child("isVipInZone").setValue(false);

                }

            }
        });


    }

    private void openInformationFragment() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create a Bundle to store the variable
                Bundle bundle = new Bundle();
                bundle.putBoolean("isVipInZone", true);

                // Create an instance of Fragment B and set its arguments
                Informations informations = new Informations();
                informations.setArguments(bundle);

                // Show Fragment SmartFeux if it already exists, otherwise add it to the stack
                Informations existingFragmentB = (Informations) getParentFragmentManager().findFragmentByTag("FragmentB");
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                if (existingFragmentB == null) {
                    transaction.add(R.id.container, informations, "informations");
                    transaction.addToBackStack(null);
                } else {
                    informations = existingFragmentB;
                }

                // Hide Fragment A
                transaction.hide(SmartFeux.this);
                transaction.show(informations);
                transaction.commit();
            }
        }, 4000);


    }

    @Override
    public void onKeyExited(String key) {

        if (!isVisible()) {
            return; // Fragment is hidden, exit the method
        }

        //check for no pointer exception
        if(isAdded() && getContext() != null){
            sendNotification("Leaving",String.format("%s Quittez la zone des feux",key));
        }

        Toast.makeText(getContext(), "leave the smart feux zone", Toast.LENGTH_SHORT).show();


        //user exit the zone
        isVipInZone.child("isVipInZone").setValue(false);
        //the lane is not exist anymore
        laneNumber.child("voie").setValue(null);


    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

        if (!isVisible()) {
            return; // Fragment is hidden, exit the method
        }

        //check for no pointer exception
        if(isAdded() && getContext() != null){

            if(userEnterToTheZone){
                sendNotification("Moving",String.format("%s Se déplacer dans la zone des feux",key));
                Toast.makeText(getContext(), "move within", Toast.LENGTH_SHORT).show();
            }
        }
        userEnterToTheZone = false;
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        snackBar(error.getMessage(), 2000, R.color.baseRed, R.drawable.ic_warning);
    }









    @Override
    public void onLoadLocationSuccess(List<MyLatLng> latLngs) {

        smartFeuxZones = new ArrayList<>();
        for(MyLatLng myLatLng : latLngs){

            LatLng convert = new LatLng(myLatLng.getLatitude(),myLatLng.getLongitude());
            smartFeuxZones.add(convert);
        }


        //Now after  smart feux zones is have data , we will call map display
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(SmartFeux.this);



        //clear map and add again
        if(mMap != null){
            mMap.clear();

            //add user marker
            addUserMarker();


            //add Circle of smart feux
            addCircleArea();




        }


    }

    @Override
    public void onLoadLocationFailed(String message) {

        snackBar(message, 2000, R.color.baseRed, R.drawable.ic_warning);

    }





    private boolean isLocationProviderEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gpsEnabled || networkEnabled;
    }





    ///   ***  other functions    ***     ///






    //send notifications function
    private void sendNotification(String title, String content) {


        if(getNotificationState()){


        String NOTIFICATION_CHANNEL_ID = "SmartDrive_multiple_locations";
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"My Notification"
                    ,NotificationManager.IMPORTANCE_DEFAULT);


            //config
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireActivity(),NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));

        Notification notification = builder.build();
        notificationManager.notify(new Random().nextInt(),notification);



        }


    }       //notification end function







    //setting dialog
    private void gpsEnableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View layout_dialog = LayoutInflater.from(getContext()).inflate(R.layout.gps_settings, null);
        builder.setView(layout_dialog);

        ConstraintLayout annuler_btn = layout_dialog.findViewById(R.id.annuler_btn);
        ConstraintLayout parametres_btn = layout_dialog.findViewById(R.id.parametres_btn);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        //btn parametres
        parametres_btn.setOnClickListener(view -> {

            // Open the device's location settings
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(settingsIntent, REQUEST_CHECK_SETTINGS);
            dialog.dismiss();
        });
        //btn annuler
        annuler_btn.setOnClickListener(view -> {
            dialog.dismiss();
            progressBar.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    if(!isLocationProviderEnabled(requireContext())) {
                        gpsEnableDialog();
                    }
                    progressBar.dismiss();


                }},2000);

        });

        //show dialog
        dialog.show();
    }





    //snackbar dialog
    private void snackBar(String message, int time, int backgroundColor, int icon) {

        CookieBar.build(getActivity())
                .setTitle("Avertissement")
                .setMessage(message)
                .setDuration(time)
                .setTitleColor(R.color.special_white)
                .setBackgroundColor(backgroundColor)
                .setCookiePosition(CookieBar.BOTTOM)
                .setIcon(icon)
                .setAction("D'accord", new OnActionClickListener() {
                    @Override
                    public void onClick() {
                        CookieBar.dismiss(getActivity());
                    }
                })
                .show();
    }







    //result from enabling gps
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_CANCELED) {


                //update user locationn
                startLocationUpdates();

                //location enabled
                snackBar("localisation active", 2000, R.color.baseGreen, R.drawable.ic_error);


                progressBar.dismiss();

            } else if (resultCode == Activity.RESULT_OK) {

                // Location provider is still disabled
                snackBar("vous devez activez votre localisation", 2000, R.color.baseRed, R.drawable.ic_warning);
                progressBar.dismiss();
                gpsEnableDialog();

            }
        }
    }



    //location update if gps turned on
    private void startLocationUpdates() {
        // Location settings are enabled, start updates
        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }
    }







    private void isUserVip(String uid, final VipCallback callback) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Boolean isVip = documentSnapshot.getBoolean("VIP");
                if (isVip != null) {
                    callback.onVipReceived(isVip);
                } else {
                    callback.onVipReceived(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onVipReceived(false);
            }
        });
    }

    // Define the callback interface
    interface VipCallback {
        void onVipReceived(boolean isVip);
    }



    //get the user Uid
    private String getUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return currentUser.getUid();
    }



    //get the emergency state
    private Boolean getEmergencyState(){
        //initialise sharedPreference state
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("emergencyState", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isEmergency",false);
    }



    //get the notification state
    private Boolean getNotificationState(){
        //initialise sharedPreference state
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isNotificationsEnabled",false);
    }






    //  *** nearby zone functions ***

    private void moveToNearestZone() {
        // Check if smartFeuxZones list is not null and contains at least one zone
        if (smartFeuxZones != null && !smartFeuxZones.isEmpty()) {
            // Get the user's current location
            Location userLocation = lastLocation;
            if (userLocation != null) {
                // Find the nearest zone
                LatLng nearestZone = findNearestZone(userLocation);
                if (nearestZone != null) {
                    // Move the map to the nearest zone
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestZone, 12.0f));
                } else {
                    // No zone found, show a message or handle the case accordingly
                    Toast.makeText(getContext(), "No zone found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // User's location is not available, show a message or handle the case accordingly
                Toast.makeText(getContext(), "User location not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            // smartFeuxZones list is null or empty, show a message or handle the case accordingly
            Toast.makeText(getContext(), "No smartFeuxZones available", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng findNearestZone(Location userLocation) {
        LatLng nearestZone = null;
        float minDistance = Float.MAX_VALUE;
        for (LatLng zone : smartFeuxZones) {
            float[] results = new float[1];
            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                    zone.latitude, zone.longitude, results);
            float distance = results[0];
            if (distance < minDistance) {
                minDistance = distance;
                nearestZone = zone;
            }
        }
        return nearestZone;
    }



} //end class
