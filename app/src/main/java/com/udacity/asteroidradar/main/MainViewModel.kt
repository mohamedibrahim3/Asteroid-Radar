package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val database = AsteroidDatabase.getInstance(app)
    private val repository = AsteroidsRepository(database)

    private val asteroidList: MediatorLiveData<List<Asteroid>> = MediatorLiveData()
    val asteroids: LiveData<List<Asteroid>>
        get() = asteroidList

    private val savedAsteroids = repository.savedAsteroids
    private val weekAsteroids = repository.weekAsteroids
    private val todayAsteroid = repository.todayAsteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid?>()
    val navigateToDetailFragment
        get() = _navigateToDetailFragment

    init {
        savedAsteroids()
    }

    fun savedAsteroids() {
        viewModelScope.launch {
            refreshAsteroids()
            getPictureOfDay()
            asteroidList.addSource(savedAsteroids) {
                asteroidList.value = it
            }
        }
    }

    fun weeksAsteroids() {
        asteroidList.removeSource(savedAsteroids)
        asteroidList.removeSource(weekAsteroids)
        asteroidList.addSource(weekAsteroids) {
            asteroidList.value = it
        }
    }

    fun todayAsteroids() {
        asteroidList.removeSource(savedAsteroids)
        asteroidList.removeSource(weekAsteroids)
        asteroidList.addSource(todayAsteroid) {
            asteroidList.value = it
        }
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

}