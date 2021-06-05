package com.test.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.test.weatherapp.adapters.WeatherAdapter
import com.test.weatherapp.constants.Constants
import com.test.weatherapp.db.Weather
import com.test.weatherapp.db.WeatherDatabase
import com.test.weatherapp.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    var responseObject: JSONObject? = null
    private lateinit var job: Job
    val cityNames: ArrayList<String> = ArrayList()
    var weatherAdapter: WeatherAdapter? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()

        recycler_view_weather.setHasFixedSize(true)
        recycler_view_weather.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        city_name_text_view.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){

                if(city_name_text_view.text.isNullOrEmpty()){
                    toast("Please enter city name")
                }else {

                    getWeatherData(city_name_text_view.text.trim().toString())
                    toast(
                        "Searching weather info of " + city_name_text_view.text.trim()
                            .toString() + ", Please wait..."
                    )
                }
                true

            }else{
                false
            }
        })

        updateData()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.select_dialog_item, cityNames)
        city_name_text_view.threshold = 1
        city_name_text_view.setAdapter(adapter)

        city_name_text_view.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val itemName = parent.getItemAtPosition(position)
            weatherAdapter?.filter?.filter(itemName.toString())
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun getWeatherData(cityName: String){

        val requestQueue = Volley.newRequestQueue(this)
        val url: String = Constants.BASE_URL + cityName + Constants.APP_ID
        val stringReq = StringRequest(Request.Method.GET, url,
            {
                response -> saveData(response, cityName)
            },
            { Log.d("TAG","error") })

        requestQueue.add(stringReq)
    }

    private fun updateData(){
        launch {
            applicationContext?.let {
                val weatherInfo = WeatherDatabase(it).getWeatherDao().getAllWeatherInfo()

                for(item in weatherInfo){
                    cityNames.add(item.city)
                }

                if(weatherInfo.size > 0) {
                    recycler_view_weather.adapter = WeatherAdapter(weatherInfo)
                    weatherAdapter = WeatherAdapter(weatherInfo)
                }
            }
        }
    }

    private fun saveData(responseData: String, city: String){
        responseObject = JSONObject(responseData)

        try {
            val dateTimeFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = dateTimeFormat.format(Date())
            val weatherObject = responseObject!!.getJSONArray("weather")
            val weatherDataObject = weatherObject.getJSONObject(0)
            val description = weatherDataObject.getString("description")
            val mainObject = responseObject!!.getJSONObject("main")
            val temp = mainObject.getDouble("temp")
            val minTemp = mainObject.getDouble("temp_min")
            val maxTemp = mainObject.getDouble("temp_max")
            val pressure = mainObject.getInt("pressure")
            val humidity = mainObject.getInt("humidity")

            //Save Weather Info
            val weatherInfo = Weather(city, description, temp, minTemp, maxTemp, pressure, humidity, currentDate)

            launch{
                applicationContext?.let {
                    WeatherDatabase.invoke(it).getWeatherDao().addWeather(weatherInfo)
                    updateData()
                }
            }

        }catch (exception: JSONException){
            exception.printStackTrace()
        }


    }
}