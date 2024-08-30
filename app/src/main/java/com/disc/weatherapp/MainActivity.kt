package com.disc.weatherapp

import android.hardware.lights.Light
import android.os.Build
import android.os.Bundle
import android.service.notification.Condition
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.disc.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 46a9c9ee8b67b7572eda9f0751ba4214

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        fetachWeatherData("kasur")

        SearchCity()
    }


    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetachWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetachWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "46a9c9ee8b67b7572eda9f0751ba4214", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humadity = responseBody.main.humidity
                    val winSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknow"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp : $maxTemp ℃"
                    binding.minTemp.text = "Min Temp : $minTemp ℃"
                    binding.temp.text = "$temperature ℃"
                    binding.humidity.text = "$humadity %"
                    binding.windSpeed.text = "$winSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hpa"
                    binding.condition.text = condition
                    binding.date.text = date()
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.cityName.text = cityName

                    changeImageConditionAccordingToWeahter(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun changeImageConditionAccordingToWeahter(condition: String) {
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottie.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottie.setAnimation(R.raw.cloud)
            }

            "Rain", "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottie.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Blizzard", "Heavy Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottie.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottie.setAnimation(R.raw.sun)
            }
        }
        binding.lottie.playAnimation()

    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp * 1000)))
    }

    fun dayName(timeStamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}

