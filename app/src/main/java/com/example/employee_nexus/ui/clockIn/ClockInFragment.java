package com.example.employee_nexus.ui.clockIn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employee_nexus.MainActivity;
import com.example.employee_nexus.databinding.FragmentClockinBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.logging.Logger;


public class ClockInFragment extends Fragment {

    private FragmentClockinBinding binding;

    FusedLocationProviderClient fusedLocationProviderClient;
    //TextView latitude,longitude;
    private double latitude,longitude;
    private final double LAT =40.342297;
    //40.3043596 Home Lat
    private final double LONG=-75.978435;
    //Home Long -76.0010342
    Button gpsCheck;
    private final static int REQUEST_CODE = 100;
    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ClockInViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ClockInViewModel.class);

        binding = FragmentClockinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        latitude = binding.clockInLat;
//        longitude = binding.clockInLong;
        gpsCheck = binding.gpsButton;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        gpsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //Permission is granted
                    getCurrentLocation();
                }else{
                    //Permission not granted
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
                }
            }
        });



        final TextView textView = binding.textClockIn;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check conditions
        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            //If permission is granted
            getCurrentLocation();
        }else{
            //If permission is denied
            Toast.makeText(getActivity(),"Permission denied",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Initialize Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Check Condition
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //When location service is enabled
            //Get Last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location!= null){
                        // When location result is not null
                        //Set latitude & longitude
//                        latitude.setText(String.valueOf(location.getLatitude()));
//                        longitude.setText(String.valueOf(location.getLongitude()));
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        double distance = distanceCheck(LAT, LONG, latitude, longitude);
                        LOGGER.info("Distance between marked location and current: "+ distance);
                        if(distance >= 0 && distance <=50){
                            //TODO Make an entry into the database to show a clock in for the employee
                        }else{

                        }
                    }else{
                        //Initialize location Request
                        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).
                                setWaitForAccurateLocation(false)
                                .setMinUpdateIntervalMillis(1000)
                                .setMaxUpdateDelayMillis(10000)
                                .build();
                        //Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                //Set latitude & longitude
//                                latitude.setText(String.valueOf(location1.getLatitude()));
//                                longitude.setText(String.valueOf(location1.getLongitude()));
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                double distance = distanceCheck(LAT, LONG, latitude, longitude);
                                LOGGER.info("Distance between marked location and current: "+ distance);
                            }
                        };
                        //Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }else{
            //When location services is not enabled, Open settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private double distanceCheck(double coordLat, double coordLong,double currLat, double currLong ){
        //Haversine Formula to determine the distance between to positions according to lat/long
        double distance = 0;
        double radius = 6378.137;
        double latCheck = currLat * Math.PI / 180 - coordLat * Math.PI / 180;
        double longCheck= currLong * Math.PI / 180 - coordLong * Math.PI / 180;
        double res = Math.sin(latCheck/2)* Math.sin(latCheck/2) +
                Math.cos(coordLat * Math.PI/180) * Math.cos(currLat*Math.PI/180) *
                Math.sin(longCheck/2) * Math.sin(longCheck/2);
        double res2 = 2 * Math.atan2(Math.sqrt(res),Math.sqrt(1-res));
        distance = radius * res2;

        return distance * 1000; // distance in meters
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}