package com.test.weatherapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    val city:String,
    val weatherDescription: String,
    val temperature: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val pressure: Int,
    val humidity: Int,
    val date: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}