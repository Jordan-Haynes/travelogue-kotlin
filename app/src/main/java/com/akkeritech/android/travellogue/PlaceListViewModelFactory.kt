package com.akkeritech.android.travellogue

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akkeritech.android.travellogue.database.PlaceRepository
import java.lang.IllegalArgumentException

class PlaceListViewModelFactory(private val dataSource: PlaceRepository, private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceListViewModel::class.java)) {
            if (!isSet) {
                val viewModel = PlaceListViewModel(dataSource, application) as T
                placeListViewModel = viewModel as PlaceListViewModel
                isSet = true
                return viewModel
            }
            else {
                return placeListViewModel as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        private var isSet = false
        private lateinit var placeListViewModel: PlaceListViewModel
    }
}