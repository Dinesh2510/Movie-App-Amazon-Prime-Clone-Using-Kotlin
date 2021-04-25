package com.movie.app.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_user_location.*
import java.util.*


class UserLocation : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_location)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getpos.setOnClickListener {
            Log.d("Debug:", CheckPermission().toString())
            Log.d("Debug:", isLocationEnabled().toString())
            RequestPermission()
            /* fusedLocationProviderClient.lastLocation.addOnSuccessListener{location: Location? ->
                 textView.text = location?.latitude.toString() + "," + location?.longitude.toString()
             }*/
            getLastLocation()
        }

    }


    fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        NewLocationData()
                    } else {
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        textView.text =
                            "You Current Location is : Long: " + location.longitude + " , Lat: " + location.latitude + "\n" + getCityName(
                                location.latitude,
                                location.longitude
                            )
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }


    fun NewLocationData() {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
            textView.text =
                "You Last Location is : Long: " + lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
        }
    }

    private fun CheckPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false

    }

    fun RequestPermission() {
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled(): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat, long, 1)

        cityName = Adress.get(0).locality +Adress.get(0).getAddressLine(0)+ "\nstr_postcode:"+ Adress.get(0).postalCode+"\nstr_Country:"+Adress.get(0).countryName+"\nstr_state:"+Adress.get(0).subAdminArea+"\nstr_locality:"+Adress.get(0).locality+Adress.get(0).featureName
        countryName = Adress.get(0).featureName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
       /* val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )
        } catch (ioException: Exception) {
            Log.e("", "Error in getting address for the location")
        }

        if (addresses == null || addresses.size == 0) {
            val errormessgae = "No address found for the location"
            Toast.makeText(this, "" + errormessgae, Toast.LENGTH_SHORT).show()
        } else {
            val address: Address = addresses[0]
            val str_postcode: String = address.getPostalCode()
            val str_Country: String = address.getCountryName()
            val str_state: String = address.getAdminArea()
            val str_district: String = address.getSubAdminArea()
            val str_locality: String = address.getLocality()
            val str_address: String = address.getFeatureName()
            Log.d(
                "Debug_ad:", "Your City: " + str_address +
                        str_locality +
                        str_district +
                        str_state +
                        str_Country +
                        str_postcode + " ; your Country " + countryName
            )}*/


        return cityName
    }

}