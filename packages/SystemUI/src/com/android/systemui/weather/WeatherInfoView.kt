/*
 * Copyright (C) 2025 the AxionAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.weather

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.systemui.res.R

class WeatherInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val weatherIcon: ImageView
    private val weatherTemp: TextView
    private val weatherDetails: TextView

    init {
        inflate(context, R.layout.keyguard_weather_area, this)
        weatherIcon = findViewById(R.id.weather_icon)
        weatherTemp = findViewById(R.id.weather_temp)
        weatherDetails = findViewById(R.id.weather_details)
    }

    fun updateWeather(
        iconResId: Int?,
        temperature: String?,
        windSpeed: String?,
        humidity: String?
    ) {
        // Update weather icon
        if (iconResId != null) {
            weatherIcon.setImageResource(iconResId)
            weatherIcon.visibility = View.VISIBLE
        } else {
            weatherIcon.visibility = View.GONE
        }

        // Update temperature
        if (!temperature.isNullOrEmpty()) {
            weatherTemp.text = temperature
            weatherTemp.visibility = View.VISIBLE
        } else {
            weatherTemp.visibility = View.GONE
        }

        // Update wind speed and humidity
        val details = listOfNotNull(
            windSpeed?.takeIf { it.isNotBlank() }?.let { "Wind: $it" },
            humidity?.takeIf { it.isNotBlank() }?.let { "Humidity: $it" }
        ).joinToString(", ")

        if (details.isNotEmpty()) {
            weatherDetails.text = details
            weatherDetails.visibility = View.VISIBLE
        } else {
            weatherDetails.visibility = View.GONE
        }
    }
}
