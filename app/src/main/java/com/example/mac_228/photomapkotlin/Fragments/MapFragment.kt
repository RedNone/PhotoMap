package com.example.mac_228.photomapkotlin.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac_228.photomapkotlin.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : BaseFragment(), OnMapReadyCallback {


    companion object {
        val TAG = "MapFragment"
    }

    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        MapsInitializer.initialize(activity)

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

    override fun onMapReady(p0: GoogleMap?) {

    }

}