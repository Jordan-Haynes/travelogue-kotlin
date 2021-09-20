package com.akkeritech.android.travellogue

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akkeritech.android.travellogue.database.PlaceRepository
import com.akkeritech.android.travellogue.databinding.FragmentPhotosBinding
import com.bumptech.glide.Glide

private const val TAG = "PhotosFragment"

class PhotosFragment : Fragment() {

    private lateinit var binding: FragmentPhotosBinding

    private var adapter: PhotoAdapter? = PhotoAdapter(emptyList())

    val placeViewModel: PlaceListViewModel by viewModels {
        val application = requireNotNull(this.activity).application
        val repository = PlaceRepository(application)
        PlaceListViewModelFactory(repository, application)
    }

    companion object {
        fun newInstance(): PhotosFragment {
            return PhotosFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photos, container, false)

        binding.placeViewModel = placeViewModel

        binding.lifecycleOwner = this

        Glide.with(this)
            .load(R.drawable.passportbg1)
            .centerCrop()
            .into(binding.photoGalleryBg)

        binding.photosRecyclerview.adapter = adapter
        binding.photosRecyclerview.layoutManager = GridLayoutManager(context, 3)

        return binding.root
    }

    private fun updateUI(photos: List<Photo>) {
        val adapter = PhotoAdapter(photos)
        binding.photosRecyclerview.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeViewModel.allReferencePhotos.observe(
            viewLifecycleOwner,
            Observer { photos ->
                photos?.let {
                    updateUI(photos)
                }
            }
        )
    }

    private class PhotoHolder(private val itemImageView: ImageView, private val photoListMembers: List<Photo>): RecyclerView.ViewHolder(itemImageView) {
        fun bind(position: Int) {
            val imageData = photoListMembers[position].photoFilename
            Glide.with(itemView)
                .load(imageData)
                .centerCrop()
                .into(itemImageView)
        }
    }

    private inner class PhotoAdapter(private val photoListMembers: List<Photo>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.photo_grid_item, parent, false) as ImageView
            return PhotoHolder(view, photoListMembers)
        }

        override fun getItemCount(): Int = photoListMembers.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(position)
        }
    }
}