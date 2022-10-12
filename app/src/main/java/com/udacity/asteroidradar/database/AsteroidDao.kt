package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.DatabaseConstants

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME} ORDER by closeApproachDate")
    fun getAll(): LiveData<List<AsteroidEntity>>



    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME} WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getWeek(startDate:String,endDate:String):LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM ${DatabaseConstants.TABLE_NAME} WHERE closeApproachDate = :date ORDER BY closeApproachDate ASC")
    fun getToday(date:String):LiveData<List<AsteroidEntity>>

}