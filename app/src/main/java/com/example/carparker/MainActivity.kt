package com.example.carparker

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.activity_location_component.*
import kotlinx.android.synthetic.main.activity_location_component.mapView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.ads.AdView



class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private  var originLocation : Location? = null
    private var parkingSpot : Marker? = null
    private var parkingPoint : Point? = null
    private var navigationMapRoute : NavigationMapRoute? = null
    val db = DatabaseHelper(this)
    private val TAG = "Samuel"
    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.access_token))
        //parkButton = findViewById(R.id.parkBtn)

        setContentView(R.layout.activity_location_component)

        //LOADS ADMOB VIA GOOGLE
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        // ON CLICK LISTENERS
        parkBtn.setOnClickListener {
            parkCar()
        }

        lastPark.setOnClickListener {
            findLastPark()
        }

        centreBtn.setOnClickListener {
            centreCamera()
        }
    }

    /**
     * Saves a new car park location based on current location component location
     */
    fun parkCar() {
        removeLastParkMarker()
        originLocation = mapboxMap.locationComponent.lastKnownLocation
        var locationc = Location("0.0")
        var loc2 : Location = originLocation ?: locationc
        originLocation = mapboxMap.locationComponent.lastKnownLocation
        parkingPoint = Point.fromLngLat(loc2.longitude, loc2.latitude)
        parkingSpot = mapboxMap.addMarker(MarkerOptions().position(LatLng(loc2.latitude, loc2.longitude)))
        addParktoSQLite(loc2.latitude, loc2.longitude)
    }

    /**
     * Removes most recent marker from map.
     */
    fun removeLastParkMarker() {
        parkingSpot?.let {
            mapboxMap.removeMarker(it)
        }
    }

    /**
     * Adds most recent parking latitude and longitude to SQLLite Database
     */
    private fun addParktoSQLite(lat: Double, long : Double) { db.insertIntoInfo(lat, long)}

    /**
     * Sets locations to find last entered carpark location.
     */
    private fun findLastPark() {
        originLocation = mapboxMap.locationComponent.lastKnownLocation
        var locationc = Location("0.0")
        var loc2 : Location = originLocation ?: locationc
        var latitude = loc2.latitude
        var longitude = loc2.longitude
        val point = Point.fromLngLat(longitude , latitude)
        getRoute(point, parkingPoint)
    }

    /**
     * Centres camera on current user location.
     */
    fun centreCamera() {
        originLocation = mapboxMap.locationComponent.lastKnownLocation
        var locationc = Location("0.0")
        var loc2 : Location = originLocation ?: locationc
        var latitude = loc2.latitude
        var longitude = loc2.longitude

        val position = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(19.0)
            .tilt(13.0)
            .build()

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }


    /**
     * Gets route from API and adds them to map layer.
     */
    fun getRoute(latLng: Point, destination : Point?) {
        Log.d(TAG, "Get Routes")
        NavigationRoute.builder(this)
            .origin(latLng)
            .destination(destination ?: Point.fromLngLat(0.0, 174.8021997))
            .profile(DirectionsCriteria.PROFILE_WALKING)
            .accessToken(getString(R.string.access_token))
            .build()
            .getRoute(object: Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>?, response: Response<DirectionsResponse>?) {
                    Log.d(TAG, response.toString())
                    val routeResponse = response ?:  return // Log.d(TAG, "Help!")
                    Log.d(TAG, "Past First Return")
                    val body = routeResponse.body() ?: return//Log.d(TAG, "Help More!")
                    Log.d(TAG, "Past Second Return")
                    if (body.routes().count() == 0) {
                        Log.d(TAG, "No Routes Found")
                        return
                    }

                    if (navigationMapRoute != null) {
                        navigationMapRoute?.removeRoute()
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, mapView, mapboxMap)
                    }
                    navigationMapRoute?.addRoute(body.routes().first())
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                }

            })
        Log.d(TAG, "Closing Route Builder")
    }

    /**
     * Sets the map style once the async feature in oncreate is complete. Displays the park from last run if available.
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {

            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            enableLocationComponent(it)
        }

        firstPark()
    }

    /**
     * Returns the last park in list when app first sets style.
     */

    private fun firstPark() {
        if (db.returnLastPark() != null) {
            parkingPoint = db.returnLastPark()
            var helpPark = parkingPoint ?: Point.fromLngLat(0.0, 0.0)
            parkingSpot = mapboxMap.addMarker(MarkerOptions().position(LatLng(helpPark.latitude(), helpPark.longitude())))
        }
    }


    /**
     * Displays the location puck and provides user location.
     */
    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.mapbox_blue))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

// Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

// Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

// Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

// Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Enable permissions to use app", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "Enable permissions to use app", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}