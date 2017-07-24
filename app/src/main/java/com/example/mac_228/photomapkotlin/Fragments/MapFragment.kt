package com.example.mac_228.photomapkotlin.Fragments


import android.content.Context.LOCATION_SERVICE
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac_228.photomapkotlin.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class MapFragment : BaseFragment(), OnMapReadyCallback,
                                    GoogleMap.OnMapLongClickListener,
                                    GoogleMap.OnInfoWindowClickListener,
                                    LocationListener{

    companion object {
        val TAG = "MapFragment"
    }

    lateinit var mapView: MapView
    lateinit var mMap: GoogleMap
    lateinit var modeFab: FloatingActionButton
    lateinit var imageFab: FloatingActionButton
    lateinit var locationManager: LocationManager
    lateinit var provider: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        MapsInitializer.initialize(activity)

        modeFab = view.findViewById(R.id.floatingActionMapMode) as FloatingActionButton
        imageFab = view.findViewById(R.id.floatingActionMapCamera) as FloatingActionButton

        modeFab.setOnClickListener {

        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
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

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.setOnMapLongClickListener(this)
        mMap.setOnInfoWindowClickListener(this)

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    private fun initializeLocationManager() {
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        provider = locationManager.getBestProvider(Criteria(), false)
        locationManager.requestLocationUpdates(provider, 1000, 0f, this@MapFragment)
    }

    private fun getLastKnowLocation(): Location{

        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        provider = locationManager.getBestProvider(Criteria(), false)

        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy) {bestLocation = l}
        }

        return bestLocation!!
    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onInfoWindowClick(p0: Marker) {

    }

    override fun onMapLongClick(p0: LatLng) {
    }

    override fun onLocationChanged(p0: Location) {

    }

}