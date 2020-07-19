package com.android.mylocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LocationListener {

    private val REQUEST_CODE = 1
    private var gpsEnable: Boolean? = null
    private var networkEnable: Boolean? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationAdapter: LocationAdapter

    private var latitude: Double? = null
    private var longitude: Double? = null

    var mLocation: Location? = null

    private var listLoc = arrayListOf<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        initRecyclerView()
        getMyLocation()
        updateUI()

    }


    override fun onStart() {
        super.onStart()
        checkPermission()
    }


    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        locationAdapter = LocationAdapter(this, listLoc)
        recyclerView.adapter = locationAdapter
    }

    override fun onLocationChanged(location: Location?) {

        location.let {
            latitude = it?.latitude
            longitude = it?.longitude
            mLocation = location
            Log.d("latitude_longitude", latitude.toString() + longitude.toString())
        }
    }

    private fun updateUI() {
        val handler = Handler()
        val r: Runnable = object : Runnable {
            override fun run() {
                if (mLocation != null) {
                    listLoc.add(mLocation!!)
                    locationAdapter.notifyItemChanged(listLoc.size -1)
                    recyclerView.scrollToPosition( listLoc.size -1)
                }
                handler.postDelayed(this, 60000)
            }
        }
        r.run()

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }


    private fun getMyLocation() {

        gpsEnable =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // Getting GPS status
        networkEnable =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // Getting network status

        if (!gpsEnable!! && !networkEnable!!) {
            showAlert()
        }


        if (gpsEnable!!) {
            if (mLocation == null) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0f, this)
            }

            if (locationManager != null) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (mLocation != null) {
                    latitude = mLocation?.latitude
                    longitude = mLocation?.longitude
                }
            }
        }

        if (networkEnable!!) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0f, this)
            if (locationManager != null) {
                mLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mLocation != null) {
                    latitude = mLocation?.latitude
                    longitude = mLocation?.longitude
                }
            }
        }

    }


    private fun checkPermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
                    )
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val alert = android.app.AlertDialog.Builder(this)
                    alert.setTitle("Location Permission needed")
                    alert.setMessage("If you denied location, you are unable to use this service..")
                    alert.setPositiveButton("Yes") { dialogInterface, which ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    alert.setNegativeButton("Cancel") { dialogInterface, which ->

                    }
                    alert.create().show()
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun showAlert() {
        val alert = android.app.AlertDialog.Builder(this)
        alert.setTitle("Attention")
        alert.setMessage("sorry, location is not available, please enable location service...")
        alert.setPositiveButton("Yes") { dialogInterface, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alert.setNegativeButton("Cancel") { dialogInterface, which ->
            Toast.makeText(this, "Unable to use this service..", Toast.LENGTH_SHORT).show()
        }
        alert.create().show()
    }

}


