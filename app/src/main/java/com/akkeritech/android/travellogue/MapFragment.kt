package com.akkeritech.android.travellogue

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.akkeritech.android.travellogue.database.PlaceRepository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
// import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentPlace: Place

    val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this.activity).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.place.observe(
            viewLifecycleOwner,
            Observer { place ->
                place?.let {
                    currentPlace = place
                }
            }
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val coordinates = LatLng(currentPlace.latitude, currentPlace.longitude)
        mMap.addMarker(
            MarkerOptions().position(coordinates).title("Current Place Marker")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates))

        updateUI(currentPlace.latitude, currentPlace.longitude)
    }


    private fun updateUI(lat: Double, long: Double) {
        if (mMap == null) {
            return
        }

        val myPoint = LatLng(lat, long)

        val bounds: LatLngBounds = LatLngBounds.Builder().include(myPoint).build()

        val margin = 100

        val update: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin)
        mMap.animateCamera(update)
    }
}