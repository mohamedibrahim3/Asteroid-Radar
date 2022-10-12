package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.AsteroidsRepository
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import kotlinx.coroutines.launch
import java.util.logging.Filter

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val database = AsteroidDatabase.getInstance(app)
    private val repository = AsteroidsRepository(database)
    val asteroids = repository.asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailFragment
        get() = _navigateToDetailFragment
    private val asteroidFilterType = MutableLiveData<AsteroidApiFilter>()
    private val mockData = false
    private val _mockAsteroids = MutableLiveData<List<Asteroid>>()

    init {
        if(mockData) {
            mockData()
        } else {
            refreshAsteroids()
            getPictureOfDay()
        }
    }

    private fun mockData() {

        val dataList = mutableListOf<Asteroid>()

        var count = 1
        while (count <= 10) {
            val data = Asteroid(
                count.toLong(),
                "codename:$count",
                "XXXX-XX-XX",
                77.0,
                88.0,
                99.8,
                66.6,
                true)
            count++
            dataList.add(data)
        }

        _mockAsteroids.postValue(dataList)
    }

    fun onAsteroidItemClick(data: Asteroid) {
        _navigateToDetailFragment.value = data
    }

    fun onDetailFragmentNavigated() {
        _navigateToDetailFragment.value = null
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = repository.getPictureOfDay()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
            //Transformations

    fun changeFilterType(filter: AsteroidApiFilter){
        asteroidFilterType.value = filter
    }
    private var _asteroids = Transformations.switchMap<AsteroidApiFilter,List<Asteroid>>(asteroidFilterType){
        when(it){
            AsteroidApiFilter.WEEK -> repository.weekAsteroids
            AsteroidApiFilter.TODAY -> repository.todayAsteroids
            else -> repository.allAsteroids
        }
    }

    val asteroidsFilter: LiveData<List<Asteroid>>
        get() = _asteroids
}