package com.akkeritech.android.travellogue

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AppComponentFactory
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.akkeritech.android.travellogue.database.PlaceRepository
import com.akkeritech.android.travellogue.databinding.FragmentAddPlaceBinding
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.lifecycle.Observer

private const val TAG = "AddPlaceFragment"

class AddPlaceFragment : Fragment() {

    private lateinit var binding: FragmentAddPlaceBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var newLocation: Location

    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val REQUEST_LOCATION_PERMISSION = 2

    private val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this.activity).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_place, container, false)

        binding.placeListViewModel = placeViewModel

        binding.lifecycleOwner = this

        if (placeViewModel.currentPlaceId == 0L) {
            binding.submitButton.setOnClickListener {
                placeViewModel.addPlace(binding.placeNameInput.text.toString(), binding.placeNotesInput.text.toString(), binding.addRatingBar.rating)
                findNavController().navigate(R.id.action_addPlaceFragment_to_placeListFragment)
            }

            binding.cameraButton.setOnClickListener {
                findNavController().navigate(R.id.action_addPlaceFragment_to_cameraFragment)
            }

            getLastKnownLocation()
        } else {
            val currentPlace = placeViewModel.getCurrentPlace()
            binding.textView2.text = "Edit Place"
            binding.submitButton.text = "SAVE"

            val currentLocation: TextView = binding.currentLocation
            val res = resources
            val locationText = String.format(
                res.getString(R.string.lat_long_text),
                Location.convert(currentPlace.latitude, Location.FORMAT_DEGREES),
                Location.convert(currentPlace.longitude, Location.FORMAT_DEGREES)
            )
            currentLocation.text = locationText

            binding.placeNameInput.setText(currentPlace.name, TextView.BufferType.EDITABLE)
            binding.placeNotesInput.setText(currentPlace.notes, TextView.BufferType.EDITABLE)
            binding.addRatingBar.rating = currentPlace.rating

            binding.submitButton.setOnClickListener {
                placeViewModel.updatePlace(binding.placeNameInput.text.toString(), binding.placeNotesInput.text.toString(), binding.addRatingBar.rating)
                findNavController().navigate(R.id.action_addPlaceFragment_to_placeDetailViewFragment)
            }

            binding.cameraButton.setOnClickListener {
                findNavController().navigate(R.id.action_addPlaceFragment_to_cameraFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.referencePhoto.observe(
            viewLifecycleOwner,
            Observer { refPhoto ->
                    Glide.with(this)
                        .load(refPhoto)
                        .into(binding.imageView4)
            }
        )

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            findNavController().navigate(R.id.action_addPlaceFragment_to_placeListFragment)
        }
    }

    private fun getLastKnownLocation() {
        if (hasLocationPermissions()) {
            try {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    // TODO location can sometimes be null, need to check this
                    newLocation = location

                    val currentLocation: TextView = binding.currentLocation
                    val res = resources
                    val locationText = String.format(
                        res.getString(R.string.lat_long_text),
                        Location.convert(newLocation.latitude, Location.FORMAT_DEGREES),
                        Location.convert(newLocation.longitude, Location.FORMAT_DEGREES)
                    )
                    currentLocation.text = locationText

                    placeViewModel.setCoordinates(newLocation.latitude, newLocation.longitude)
                }
            } catch (exception: SecurityException) {
                ActivityCompat.requestPermissions(requireActivity(), LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSION)
            }
        } else {
            // requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSION)
            ActivityCompat.requestPermissions(requireActivity(), LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireActivity(), LOCATION_PERMISSIONS[0]
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

}