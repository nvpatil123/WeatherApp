package com.test.weatherapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert
    suspend fun addWeather(weather: Weather)

    @Query("select * from weather where city = :city")
    suspend fun getWeather(city: String): Weather

    @Query("select * from weather")
    suspend fun getAllWeatherInfo(): List<Weather>

}