package de.thu.recipebook.services;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;
import java.util.Locale;

public class LocationService extends Service {
    FusedLocationProviderClient locationClient;
    Geocoder geocoder;

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.ENGLISH);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle = new Bundle();

        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (addresses != null && addresses.size() > 0) {
                                bundle.putString("country", addresses.get(0).getCountryName());
                                receiver.send(1, bundle);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LocationService", "Error trying to get last GPS location");
                        e.printStackTrace();
                    });
        } catch (SecurityException se) {
            se.printStackTrace();
        }

        return START_STICKY;
    }
}
