package com.akkeritech.android.travellogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.akkeritech.android.travellogue.database.PlaceRepository
import com.akkeritech.android.travellogue.databinding.FragmentPlaceBinding
import com.akkeritech.android.travellogue.databinding.FragmentPlaceListBinding
import com.bumptech.glide.Glide

private const val TAG = "PlaceListFragment"

class PlaceListFragment : Fragment() {

    private lateinit var binding: FragmentPlaceListBinding
    private var adapter: PlaceAdapter? = PlaceAdapter(emptyList())

    val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this.activity).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    companion object {
        fun newInstance(): PlaceListFragment {
            return PlaceListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_list, container, false)

        binding.placeListViewModel = placeViewModel

        binding.lifecycleOwner = this

        Glide.with(this)
            .load(R.drawable.passportbg1)
            .centerCrop()
            .into(binding.placeListBg)

        binding.placeList.adapter = adapter

        return binding.root
    }

    private fun updateUI(places: List<Place>) {
        val adapter = PlaceAdapter(places)
        binding.placeList.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.allPlaces.observe(
            viewLifecycleOwner,
            Observer { places ->
                places?.let {
                    updateUI(places)
                }
            }
        )
    }

    private class PlaceHolder private constructor(val binding: FragmentPlaceBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var placeId: Long = 0

        fun bind(item: Place) {
            binding.nameText.text = item.name
            binding.coordinatesText.text = item.latitude.toString()

            binding.coordinatesText.text = "${item.latitude}, ${item.longitude}"

            // TODO Update RatingsBar
            binding.ratingBar.rating = item.rating

            placeId = item.placeId
            if (item.referencePhoto != null) {
                Glide.with(itemView)
                    .load(item.referencePhoto)
                    .into(binding.imageView)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val action = PlaceListFragmentDirections.actionPlaceListFragmentToPlaceDetailViewFragment(placeId)
            view.findNavController().navigate(action)
        }

        companion object {
            fun from(parent: ViewGroup): PlaceHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentPlaceBinding.inflate(layoutInflater, parent, false)
                return PlaceHolder(binding)
            }
        }
    }

    private inner class PlaceAdapter(private val placeListMembers: List<Place>) : RecyclerView.Adapter<PlaceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
            return PlaceHolder.from(parent)
        }

        override fun getItemCount(): Int = placeListMembers.size

        override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
            val placeListMember = placeListMembers[position]
            holder.bind(placeListMember)
        }
    }

    override fun onStart() {
        super.onStart()
        placeViewModel.resetCurrentPlace()
    }
}