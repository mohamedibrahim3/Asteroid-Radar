package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.DatabaseConstants

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME} ORDER by closeApproachDate")
    fun getAll(): LiveData<List<AsteroidEntity>>


}