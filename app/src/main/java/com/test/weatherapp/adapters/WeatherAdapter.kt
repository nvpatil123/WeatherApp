package com.test.weatherapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp.R
import com.test.weatherapp.db.Weather
import kotlinx.android.synthetic.main.weather_info_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter(val weatherData: List<Weather>) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>(), Filterable{

    var weatherList = ArrayList<Weather>()

    init {
        weatherList = weatherData.toList() as ArrayList<Weather>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): WeatherAdapter.WeatherViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.weather_info_layout, parent,
                false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherAdapter.WeatherViewHolder, position: Int) {

        holder.view.city_title.text = "City: " + weatherData[position].city
        holder.view.city_descr.text = "Description: " + weatherData[position].weatherDescription
        holder.view.city_temp.text = "Temp: " + weatherData[position].temperature
        holder.view.city_max_temp.text = "MaxTemp: " + weatherData[position].maxTemp
        holder.view.city_min_temp.text = "MinTemp: " + weatherData[position].minTemp
        holder.view.city_pressure.text = "Pressure: " + weatherData[position].pressure
        holder.view.city_humidity.text = "Humidity: " + weatherData[position].humidity
        holder.view.current_date_time.text = "Date: " + weatherData[position].date
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    fun getItems(): List<Weather>{
        return weatherData
    }


    class WeatherViewHolder(val view: View): RecyclerView.ViewHolder(view){

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    weatherList = weatherData as ArrayList<Weather>
                } else {
                    val resultList = ArrayList<Weather>()
                    for (row in weatherData) {
                        if (row.city.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    weatherList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = weatherList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                weatherList = results?.values as ArrayList<Weather>
                notifyDataSetChanged()
            }

        }
    }

}