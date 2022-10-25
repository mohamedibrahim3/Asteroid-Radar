package com.udacity.asteroidradar

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asAsteroidEntities
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asAsteroids
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidDatabase) {
    //Transformations
    private val startDate = Calendar.getInstance()
    private val endDate = Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, 7) }
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asAsteroids()
        }

    val todayAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getToday(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(startDate.time)
        )
    ) {
        it.asAsteroids()
    }

    val weekAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getWeek(
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startDate.time), SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(endDate.time)
        )
    ) {
        it.asAsteroids()
    }

    val savedAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAll()) {
            it.asAsteroids()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids = AsteroidApi.getAsteroids()
            database.asteroidDao.insertAll(asteroids.asAsteroidEntities())
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay {
        lateinit var pictureOfDay: PictureOfDay
        withContext(Dispatchers.IO) {
            pictureOfDay = AsteroidApi.getPictureOfDay()
        }
        return pictureOfDay
    }
}