package com.example.busbuddy;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabaseRef;
    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get the reference of the Firebase database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("locations");

        // Initialize the GeofencingClient
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Read the data from the Firebase database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the map before updating the new location
                mMap.clear();

                // Get the latest location from the database
                LocationData location = dataSnapshot.getValue(LocationData.class);

                // If the location is not null, add a marker to it and move the camera to that location
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    // Check if the current location is within the geofence area
                    if (isInsideGeofence1(latLng)) {
                        // Send a notification if the user is inside the geofence
                        sendNotification("You have reached stop 1");
                    }
                    else if (isInsideGeofence2(latLng)){
                        sendNotification("You have reached stop 2");

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Display an error message if the data cannot be retrieved from the database
                Toast.makeText(MapsActivity.this, "Failed to read location data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the given location is inside the geofence area
    private boolean isInsideGeofence1(LatLng latLng) {
        // Define the geofence area
        double geofenceLat = 14.9614914;
        double geofenceLng = 74.7080299;
        float geofenceRadius = 50; // in meters

        // Calculate the distance between the current location and the geofence center
        float[] distance = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude, geofenceLat, geofenceLng, distance);

        // Check if the distance is within the geofence radius
        return distance[0] <= geofenceRadius;
    }

    private boolean isInsideGeofence2(LatLng latLng) {
        // Define the geofence area
        double geofenceLat = 14.96053;
        double geofenceLng = 74.70640;
        float geofenceRadius = 50; // in meters

        // Calculate the distance between the current location and the geofence center
        float[] distance = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude, geofenceLat, geofenceLng, distance);

        // Check if the distance is within the geofence radius
        return distance[0] <= geofenceRadius;
    }

    // Method to send a notification to the user
    private void sendNotification(String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tracking Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(this, MapsActivity.class);
        mGeofencePendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(mGeofencePendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);

        notificationManager.notify(0, builder.build());

    }
}