package com.example.mac_228.photomapkotlin.Fragments


import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
<<<<<<< HEAD
import android.content.Intent
import android.content.res.ColorStateList
=======
>>>>>>> b8a3f038500934b8b024bf53edc2dd435106f7e3
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
<<<<<<< HEAD
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.Log
=======
>>>>>>> b8a3f038500934b8b024bf53edc2dd435106f7e3
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.mac_228.photomapkotlin.BuildConfig
import com.example.mac_228.photomapkotlin.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MapFragment : BaseFragment(), OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationListener {

    companion object {
        val TAG = "MapFragment"
    }

    var FIRST_UPDATE = false
    var NAVIGATION_MODE = false

    val BUTTON_PRESS = 1
    val LONG_PRESS_MAP = 0

    private val GPS_IS_ON = 2

    lateinit var mapView: MapView
    lateinit var mMap: GoogleMap
    lateinit var modeFab: FloatingActionButton
    lateinit var imageFab: FloatingActionButton
    lateinit var locationManager: LocationManager
    lateinit var provider: String
    var location: Location? = null
    lateinit var mLayout: RelativeLayout
    var fileUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        MapsInitializer.initialize(activity)

        mLayout = view.findViewById(R.id.mapRelative) as RelativeLayout

        modeFab = view.findViewById(R.id.floatingActionMapMode) as FloatingActionButton
        imageFab = view.findViewById(R.id.floatingActionMapCamera) as FloatingActionButton

        modeFab.setOnClickListener { changeMode() }
        imageFab.setOnClickListener { openPictureDialog(BUTTON_PRESS) }

        setHasOptionsMenu(true)

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        initializeLocationManager()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mMap.isMyLocationEnabled = true
        }
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false

        location = getLastKnowLocation() ?: return
        onLocationChanged(location)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val action_logout = menu.findItem(R.id.action_logOut)
        val action_settings = menu.findItem(R.id.action_settings)
        val action_search = menu.findItem(R.id.action_search)

        action_logout.isVisible = true
        action_settings.isVisible = true
        action_search.isVisible = false
    }


    fun initializeImageUri() {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera")
        val image: File?

        image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        if (image != null) {
            fileUri = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    image)
        }
    }

    private fun openPictureDialog(typeOfCall: Int) {
        var pictureDialog = PictureDialog(this)

        if (typeOfCall == BUTTON_PRESS) {
            pictureDialog.setTargetFragment(this, BUTTON_PRESS)
            pictureDialog.show(fragmentManager, "asdasd")
        }
        if (typeOfCall == LONG_PRESS_MAP) {
            pictureDialog.setTargetFragment(this, LONG_PRESS_MAP)
            pictureDialog.show(fragmentManager, "dsdad")
        }

    }


    private fun checkGPS() {
        val statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d(TAG, statusOfGPS.toString())
        if (!statusOfGPS) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.gps_off)
                    .setMessage(R.string.gps_message)
                    .setNegativeButton(R.string.gps_negative_button) { dialogInterface, _ -> dialogInterface.cancel() }
                    .setPositiveButton(R.string.gps_positive_button) { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        this@MapFragment.startActivityForResult(intent, GPS_IS_ON)
                    }.create().show()
        }
    }

    private fun changeMode() {
        checkGPS()
        location = getLastKnowLocation()

        if (location == null) {
            Snackbar.make(mLayout, R.string.locationIsNull, Snackbar.LENGTH_SHORT).show()
            return
        }

        if (!NAVIGATION_MODE) {
            NAVIGATION_MODE = true
            mMap.uiSettings.isScrollGesturesEnabled = false
            modeFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorFabEnabled))
            onLocationChanged(location)
        } else {
            NAVIGATION_MODE = false
            mMap.uiSettings.isScrollGesturesEnabled = true
            modeFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorFabDisabled))
        }
    }

    private fun initializeLocationManager() {
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        provider = locationManager.getBestProvider(Criteria(), false)
        if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            locationManager.requestLocationUpdates(provider, 1000, 0f, this@MapFragment)
        }
    }

    private fun getLastKnowLocation(): Location? {
        if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
            return null

        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
<<<<<<< HEAD
        provider = locationManager.getBestProvider(Criteria(), false)

        return providers.map { locationManager.getLastKnownLocation(it) }.maxBy { it.accuracy }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GPS_IS_ON -> {
                location = getLastKnowLocation()
                if (location != null) {
                    onLocationChanged(location)
                }
            }
        }
=======
        val bestLocation: Location?
        provider = locationManager.getBestProvider(Criteria(), false)

        bestLocation = providers.map { locationManager.getLastKnownLocation(it) }.maxBy { it.accuracy }

        return bestLocation
>>>>>>> b8a3f038500934b8b024bf53edc2dd435106f7e3
    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onInfoWindowClick(p0: Marker) {}

    override fun onMapLongClick(p0: LatLng) {  openPictureDialog(LONG_PRESS_MAP) }

    override fun onLocationChanged(p0: Location?) {

        if (p0 == null) {
            return
        }

        if (!FIRST_UPDATE) {
            FIRST_UPDATE = true
            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(p0.latitude, p0.longitude))      // Sets the center of the map to location user
                    .zoom(17f)                   // Sets the zoom
                    .tilt(40f)                   // Sets the tilt of the camera to 30 degrees
                    .build()                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        if (NAVIGATION_MODE) {
            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(p0.latitude, p0.longitude))      // Sets the center of the map to location user
                    .zoom(17f)                   // Sets the zoom
                    .tilt(40f)                   // Sets the tilt of the camera to 30 degrees
                    .build()                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

}

