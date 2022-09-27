package de.thu.recipebook.services

import android.app.Service
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class LocationService : Service() {
    var locationClient: FusedLocationProviderClient? = null
    var geocoder: Geocoder? = null

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.ENGLISH)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val receiver = intent.getParcelableExtra<ResultReceiver>("receiver")
        val bundle = Bundle()

        try {
            locationClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        var addresses: List<Address>? = null
                        try {
                            addresses =
                                geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (addresses != null && addresses.isNotEmpty()) {
                            bundle.putString("country", addresses[0].countryName)
                            receiver!!.send(1, bundle)
                        }
                    }
                }
                .addOnFailureListener { e: Exception ->
                    Log.e("LocationService", "Error trying to get last GPS location")
                    e.printStackTrace()
                }
        } catch (se: SecurityException) {
            se.printStackTrace()
        }

        return START_STICKY
    }
}