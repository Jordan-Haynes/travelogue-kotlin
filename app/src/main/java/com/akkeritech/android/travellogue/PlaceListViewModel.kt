package com.akkeritech.android.travellogue

import android.app.Application
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.akkeritech.android.travellogue.database.PlaceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PlaceListViewModel"

class PlaceListViewModel(private val repository: PlaceRepository, application: Application) : AndroidViewModel(application), Observable {

    private var _place: MutableLiveData<Place> = MutableLiveData<Place>()
    val place: LiveData<Place>
        get() = _place

    private var currentPlace: Place = Place()

    private var _referencePhoto: MutableLiveData<String> = MutableLiveData<String>()
    val referencePhoto: LiveData<String>
        get() = _referencePhoto

    private var _placeLatitude: MutableLiveData<Double> = MutableLiveData<Double>()
    val placeLatitude: LiveData<Double>
        get() = _placeLatitude

    private var _placeLongitude: MutableLiveData<Double> = MutableLiveData<Double>()
    val placeLongitude: LiveData<Double>
        get() = _placeLongitude

    var thePhotoPath: String = ""
    var currentPlaceId: Long = 0L

    val allPlaces: LiveData<List<Place>> = repository.allPlaces.asLiveData()
    var allReferencePhotos: LiveData<List<Photo>> = repository.getPlacePhotos(currentPlaceId).asLiveData()

    fun setCurrentPlace(placeId: Long) {
        currentPlaceId = placeId
        val myScope = CoroutineScope(Dispatchers.Main)
        myScope.launch {
            _place.value = repository.getCurrentPlace(placeId)
            currentPlace = repository.getCurrentPlace(placeId)
        }
        allReferencePhotos = repository.getPlacePhotos(currentPlaceId).asLiveData()
    }

    fun getCurrentPlace(): Place {
        return currentPlace
    }

    fun resetCurrentPlace() {
        currentPlaceId = 0L
        thePhotoPath = ""
        _referencePhoto.value = ""
    }

    fun addPlace(newName: String, newNotes: String, newRating: Float) {
        val latitude = placeLatitude.value as Double
        val longitude = placeLongitude.value as Double
        val placeReferencePhoto = referencePhoto.value.toString()
        val newPlace =
            Place(0, newName, latitude, longitude, newNotes, newRating, placeReferencePhoto)

        val myScope = CoroutineScope(Dispatchers.Main)
        myScope.launch {
            val placeId = repository.addPlace(newPlace)
            addRefPhoto(placeReferencePhoto, placeId)
        }
    }

    fun updatePlace(updatedName: String, updatedNotes: String, updatedRating: Float) {
        val updatedPlace = currentPlace
        if (updatedName != "") {
            updatedPlace.name = updatedName
        }
        if (updatedNotes != "") {
            updatedPlace.notes = updatedNotes
        }
        updatedPlace.rating = updatedRating

        repository.updatePlace(updatedPlace)
    }

    fun deletePlace() {
        repository.deletePlace(currentPlace)
        repository.deletePlacePhotos(currentPlace.placeId)
    }

    fun updatePhotoFile(photoFile: String) {
        _referencePhoto.value = photoFile
        thePhotoPath = photoFile

        if (currentPlaceId != 0L) {
            addNonRefPhoto()
        }
    }

    fun addRefPhoto(photoFile: String, placeId: Long) {
        val newPhoto = Photo(0, placeId, true, photoFile)
        repository.addPhoto(newPhoto)
    }

    fun addNonRefPhoto() {
        val newPhoto = Photo(0, currentPlaceId, false, thePhotoPath)
        repository.addPhoto(newPhoto)
    }

    fun setCoordinates(lat: Double, long: Double) {
        _placeLatitude.value = lat
        _placeLongitude.value = long
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}