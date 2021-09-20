package com.akkeritech.android.travellogue

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.akkeritech.android.travellogue.database.PlaceRepository
import com.akkeritech.android.travellogue.databinding.FragmentPlaceDetailViewBinding
import com.bumptech.glide.Glide

private const val TAG = "PlaceDetailViewFragment"

class PlaceDetailViewFragment : Fragment() {

    private lateinit var binding: FragmentPlaceDetailViewBinding

    private val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this.activity).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    val args: PlaceDetailViewFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_detail_view, container, false)

        binding.placeListViewModel = placeViewModel

        binding.lifecycleOwner = this

        Glide.with(this)
            .load(R.drawable.passportbg1)
            .centerCrop()
            .into(binding.detailViewBg)

        if (placeViewModel.currentPlaceId == 0L) {
            val id = args.placeId
            placeViewModel.setCurrentPlace(id)
        } else {
            placeViewModel.setCurrentPlace(placeViewModel.currentPlaceId)
        }

        binding.toCameraButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_placeDetailViewFragment_to_cameraFragment)
        }

        binding.photosButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_placeDetailViewFragment_to_photosFragment)
        }

        binding.mapButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_placeDetailViewFragment_to_mapFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.place.observe(
            viewLifecycleOwner,
            Observer { place ->
                place?.let {
                    binding.coordinatesText.text = "${place.latitude}, ${place.longitude}"
                    Glide.with(this)
                        .load(place.referencePhoto)
                        .into(binding.imageView)
                }
            }
        )
    }
}