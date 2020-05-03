package com.mahidol.dietplanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_location.*
import java.io.IOException


class LocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val TAG: String = "Location Fragment"

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    lateinit var placesClient: PlacesClient
    var zoom = 17.0f

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        // Initialize the SDK
        Places.initialize(this,getString(R.string.google_maps_key))

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        //GoGoGo
        mapFragment.getMapAsync(this)
        setupPlacesAutoComplete()
        findViewById<Button>(R.id.btn_current_place).setOnClickListener {
            findCurrentPlace()
        }

    }

    private fun findCurrentPlace() {
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            lastLocation = location
            val currentLatLong = LatLng(location.latitude, location.longitude)
            placeMarkerOnMap(currentLatLong)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, zoom))


        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //current location button
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()

    }

    //CHECK PERMISSION ACCESS_FINE_LOCATION
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location!=null){
                findCurrentPlace()
            }

        }
    }

    private fun setupPlacesAutoComplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(this@LocationActivity, ""+ p0.latLng,Toast.LENGTH_SHORT).show()
                val currentLatLong = p0.latLng!!
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 17.0f))

                // Start another activity with position
                val intent = Intent(this@LocationActivity, ResultActivity::class.java)
                intent.putExtra("latitude", currentLatLong.latitude.toString())
                startActivity(intent)


            }

            override fun onError(p0: Status) {
                Toast.makeText(this@LocationActivity, ""+p0.statusMessage,Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onMarkerClick(p0: Marker?) = false

    //easy place marker
    private fun placeMarkerOnMap(location: LatLng) {
        mMap.clear()
        val titleStr = getAddress(location)
        val markerOptions = MarkerOptions()
            .position(location)
            .title(titleStr)
        markerOptions.title(titleStr)


        mMap.addMarker(markerOptions)
    }

    //Show address
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }


}

