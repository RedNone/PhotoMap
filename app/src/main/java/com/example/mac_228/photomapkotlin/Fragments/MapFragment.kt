package com.example.mac_228.photomapkotlin.Fragments


import android.app.AlertDialog
import android.content.*
import android.content.Context.LOCATION_SERVICE
import android.content.res.ColorStateList
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
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.Log
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.mac_228.photomapkotlin.Activity.*
import com.example.mac_228.photomapkotlin.Adapters.SettingsAdapter
import com.example.mac_228.photomapkotlin.BuildConfig
import com.example.mac_228.photomapkotlin.FireBaseManager
import com.example.mac_228.photomapkotlin.ImageType
import com.example.mac_228.photomapkotlin.Models.PhotoModel
import com.example.mac_228.photomapkotlin.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapFragment : BaseFragment(), OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationListener {

    companion object {
        val TAG = "MapFragment"
    }

    var FIRST_UPDATE = false
    var NAVIGATION_MODE = false


    val LONG_PRESS_MAP = 0
    val BUTTON_PRESS = 1
    val EXISTING_PHOTO = 2

    private val GPS_IS_ON = 2

    lateinit var locationOfImage: LatLng

    lateinit var mapView: MapView
    lateinit var mMap: GoogleMap
    lateinit var modeFab: FloatingActionButton
    lateinit var imageFab: FloatingActionButton
    lateinit var locationManager: LocationManager
    lateinit var provider: String
    var location: Location? = null
    lateinit var mLayout: RelativeLayout
    var fileUri: Uri? = null

    lateinit var br: BroadcastReceiver

    private var sPreferences: SharedPreferences? = null
    lateinit var markerList: MutableList<Marker>
    lateinit var newData: MutableList<PhotoModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        MapsInitializer.initialize(activity)

        markerList = ArrayList()
        sPreferences = activity.getSharedPreferences(SettingsAdapter.APP_PREFERENCE, Context.MODE_PRIVATE)

        mLayout = view.findViewById(R.id.mapRelative) as RelativeLayout

        modeFab = view.findViewById(R.id.floatingActionMapMode) as FloatingActionButton
        imageFab = view.findViewById(R.id.floatingActionMapCamera) as FloatingActionButton

        modeFab.setOnClickListener { changeMode() }
        imageFab.setOnClickListener { openPictureDialog(BUTTON_PRESS) }

        setHasOptionsMenu(true)

        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (FireBaseManager.newDataList.isNotEmpty()) {
                    Log.d(TAG, "BroadCast")
                    prepareNewData()
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        initializeLocationManager()
        if (!FireBaseManager.newDataList.isNotEmpty()) {
            Snackbar.make(mLayout, R.string.data_loadind, Snackbar.LENGTH_LONG).show()
        }
        activity.registerReceiver(br, IntentFilter(MainActivity.BROADCAST_ACTION))
        if (FireBaseManager.dataStatusForMap === 1) {
            prepareNewData()
        } else {
            if (SettingsActivity.SETTINGS_UPDATE === true) {
                prepareNewData()
                SettingsActivity.SETTINGS_UPDATE = false
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        locationManager.removeUpdates(this)
        activity.unregisterReceiver(br)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun prepareNewData() {
        if (!FireBaseManager.newDataList.isNotEmpty()) {
            return
        }
        Log.d(TAG, "PrepareNewData")
        val friends = sPreferences?.getBoolean(getString(R.string.friends), true)

        val nature = sPreferences?.getBoolean(getString(R.string.nature), true)

        val default_type = sPreferences?.getBoolean(getString(R.string.default_type), true)

        val food = sPreferences?.getBoolean(getString(R.string.food), true)


        newData = ArrayList<PhotoModel>()

        for (obj in FireBaseManager.newDataList) {
            newData.add(obj.copy())
        }


        var i = 0
        while (i < newData.size) {
            val model = newData[i]
            if (model.type.equals(getString(R.string.friends))) {
                if (friends != true) {
                    newData.removeAt(i)
                    i--
                }
            }
            if (model.type.equals(getString(R.string.nature))) {
                if (nature != true) {
                    newData.removeAt(i)
                    i--
                }
            }
            if (model.type.equals(getString(R.string.default_type))) {
                if (default_type != true) {
                    newData.removeAt(i)
                    i--
                }
            }
            if (model.type.equals(getString(R.string.food))) {
                if (food != true) {
                    newData.removeAt(i)
                    i--
                }
            }
            i++
        }


        for (i in newData.indices) {
            newData[i].time = formatTime(newData[i].time)
        }


        addMarkers(newData)
        FireBaseManager.dataStatusForMap = 0

    }

    private fun addMarkers(newData: List<PhotoModel>) {

        if (!markerList.isEmpty()) {
            for (i in markerList.indices) {
                markerList[i].remove()
            }

        }
        markerList.clear()
        for (i in newData.indices) {
            Log.d(TAG, newData[i].uri.toString())

            val marker = mMap.addMarker(MarkerOptions()
                    .position(getLatLng(newData[i].coordinats)))

            if (newData[i].type.equals(getString(R.string.friends))) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            if (newData[i].type.equals(getString(R.string.nature))) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
            if (newData[i].type.equals(getString(R.string.default_type))) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            }
            if (newData[i].type.equals(getString(R.string.food))) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            }

            marker.title = newData[i].id.toString()
            markerList.add(marker)

        }
    }

    private fun formatTime(time: String): String {

        var newTime: String? = null
        val format = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH)
        try{
            val date = format.parse(time)
            val newformat = SimpleDateFormat("yyyy'.'MM'.'dd")
            newTime = newformat.format(date)
        } catch (e: java.text.ParseException) {
            Log.e(TAG, e.printStackTrace().toString())
        }

        return newTime as String
    }


    private fun getLatLng(str: String): LatLng {
        val first = str.indexOf("(")
        val second = str.indexOf(",")
        val last = str.indexOf(")")

        val lat = java.lang.Double.parseDouble(str.substring(first + 1, second))
        val lng = java.lang.Double.parseDouble(str.substring(second + 1, last))
        return LatLng(lat, lng)
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

        mMap.setInfoWindowAdapter(MyInfoWindowAdapter())

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

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
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
        } else {
            Snackbar.make(mLayout, R.string.error, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun openPictureDialog(typeOfCall: Int) {

        if (!activity.checkNetwork()) {
            Snackbar.make(mLayout, R.string.network_disable, Snackbar.LENGTH_SHORT).show()
            return
        }

        val pictureDialog = PictureDialog(this)

        if (typeOfCall == BUTTON_PRESS) {
            pictureDialog.setTargetFragment(this, BUTTON_PRESS)
            pictureDialog.show(fragmentManager, pictureDialog.javaClass.name)
        }
        if (typeOfCall == LONG_PRESS_MAP) {
            pictureDialog.setTargetFragment(this, LONG_PRESS_MAP)
            pictureDialog.show(fragmentManager, pictureDialog.javaClass.name)
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
        if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return null
        }

        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        provider = locationManager.getBestProvider(Criteria(), false)

        return providers.map { locationManager.getLastKnownLocation(it) }.filter { it != null }.maxBy { it.accuracy }
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
            BUTTON_PRESS -> {
                if (data != null) {
                    if (!activity.checkNetwork() && location == null) {
                        Snackbar.make(mLayout, R.string.error, Snackbar.LENGTH_SHORT).show()
                        return
                    }
                    if (data.data != null) {

                        startActivity(prepareImageIntent(data.data.toString(), BUTTON_PRESS, ImageType.GALLERY_TYPE))
                    }

                } else {
                    if (fileUri != null) {
                        startActivity(prepareImageIntent(fileUri.toString(), BUTTON_PRESS, ImageType.CAMERA_TYPE))
                        fileUri = null
                    }
                }

            }
            LONG_PRESS_MAP -> {
                if (data != null) {
                    if (!activity.checkNetwork() && location == null) {
                        Snackbar.make(mLayout, R.string.error, Snackbar.LENGTH_SHORT).show()
                        return
                    }
                    if (data.data != null) {
                        startActivity(prepareImageIntent(data.data.toString(), LONG_PRESS_MAP, ImageType.GALLERY_TYPE))
                    }
                } else {
                    if (fileUri != null) {
                        startActivity(prepareImageIntent(fileUri.toString(), BUTTON_PRESS, ImageType.CAMERA_TYPE))
                        fileUri = null
                    }
                }

            }
        }
    }

    private fun prepareImageIntent(data: String, flag: Int, type: ImageType): Intent {
        val intent = Intent(activity, PhotoDetailsActivity::class.java)

        if (flag == BUTTON_PRESS) {
            if (type == ImageType.GALLERY_TYPE) {
                intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.NEW_IMAGE_BUTTON)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_URI, data)
                intent.putExtra(PhotoDetailsActivity.TYPE_OF_IMAGE, ImageType.GALLERY_TYPE)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_LOCATION, getLatLng())

            }
            if (type == ImageType.CAMERA_TYPE) {
                intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.NEW_IMAGE_BUTTON)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_URI, data)
                intent.putExtra(PhotoDetailsActivity.TYPE_OF_IMAGE, ImageType.CAMERA_TYPE)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_LOCATION, getLatLng())
            }
        }
        if (flag == LONG_PRESS_MAP) {
            if (type == ImageType.GALLERY_TYPE) {
                intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.NEW_IMAGE_LONGPRESS)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_URI, data)
                intent.putExtra(PhotoDetailsActivity.TYPE_OF_IMAGE, ImageType.GALLERY_TYPE)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_LOCATION, locationOfImage.toString())
            }
            if (type == ImageType.CAMERA_TYPE) {
                intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.NEW_IMAGE_LONGPRESS)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_URI, data)
                intent.putExtra(PhotoDetailsActivity.TYPE_OF_IMAGE, ImageType.CAMERA_TYPE)
                intent.putExtra(PhotoDetailsActivity.NEW_IMAGE_LOCATION, locationOfImage.toString())
            }

        }
        if (flag == EXISTING_PHOTO) {
            intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.EXISTING_PHOTO)
            intent.putExtra(PhotoDetailsActivity.EXISTING_PHOTO_ID, data)
        }
        return intent
    }

    private fun getLatLng(): String {
        return "lat/lng:(${location?.latitude},${location?.longitude})"

    }

    override fun onProviderEnabled(p0: String?) {
        location = getLastKnowLocation() ?: null
        if (location != null) {
            onLocationChanged(location)
        }
    }

    override fun onProviderDisabled(p0: String?) {
        checkGPS()
        FIRST_UPDATE = false
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onInfoWindowClick(marker: Marker) {
        startActivity(prepareImageIntent(marker.title.toString(), EXISTING_PHOTO, ImageType.EXISTING_TYPE))
    }

    override fun onMapLongClick(location: LatLng) {
        openPictureDialog(LONG_PRESS_MAP)
        locationOfImage = location
    }

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

    internal inner class MyInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val myContentsView: View = activity.layoutInflater.inflate(R.layout.marker_layout, null)

        override fun getInfoContents(marker: Marker): View {
            var obj: PhotoModel? = null
            for (model in newData) {
                if (model.id === marker.title.toInt()) {
                    obj = model
                    break
                }
            }

            val tvTitle = myContentsView.findViewById(R.id.textViewMarkerData) as TextView
            tvTitle.text = obj?.text
            val tvSnippet = myContentsView.findViewById(R.id.textViewMarkerDate) as TextView
            tvSnippet.text = obj?.time

            val imageView = myContentsView.findViewById(R.id.imageViewMarker) as ImageView
            imageView.setImageURI(obj?.uri)


            return myContentsView
        }

        override fun getInfoWindow(marker: Marker): View? {
            // TODO Auto-generated method stub
            return null
        }

    }

}

