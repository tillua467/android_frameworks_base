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
import android.os.UserHandle
import android.provider.Settings
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.systemui.res.R
import com.android.internal.util.crdroid.OmniJawsClient

import com.android.systemui.Dependency
import com.android.systemui.plugins.statusbar.StatusBarStateController

class WeatherViewController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), OmniJawsClient.OmniJawsObserver {

    private val weatherIcon: ImageView
    private val weatherTemp: TextView
    private val weatherDetails: TextView
    private val mWeatherClient: OmniJawsClient

    init {
        inflate(context, R.layout.keyguard_weather_area, this)
        weatherIcon = findViewById(R.id.weather_icon)
        weatherTemp = findViewById(R.id.weather_temp)
        weatherDetails = findViewById(R.id.weather_details)

        mWeatherClient = OmniJawsClient(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mWeatherClient.addObserver(this)
        queryAndUpdateWeather()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mWeatherClient.removeObserver(this)
    }

    private fun queryAndUpdateWeather() {
        if (mWeatherClient.isOmniJawsEnabled) {
            mWeatherClient.queryWeather()
        } else {
            weatherIcon.visibility = View.GONE
            weatherTemp.visibility = View.GONE
            weatherDetails.visibility = View.GONE
        }
    }

    override fun weatherUpdated() {
        val weatherInfo = mWeatherClient.weatherInfo
        if (weatherInfo != null) {
            // Update weather icon
            val icon: Drawable? = mWeatherClient.getWeatherConditionImage(weatherInfo.conditionCode)
            if (icon != null) {
                weatherIcon.setImageDrawable(icon)
                weatherIcon.visibility = View.VISIBLE
            } else {
                weatherIcon.visibility = View.GONE
            }

            // Update temperature
            val temp = weatherInfo.temp
            if (!temp.isNullOrEmpty()) {
                weatherTemp.text = temp
                weatherTemp.visibility = View.VISIBLE
            } else {
                weatherTemp.visibility = View.GONE
            }

            // Update wind speed and humidity
            val wind = weatherInfo.windSpeed
            val humidity = weatherInfo.humidity
            val details = listOfNotNull(
                wind?.takeIf { it.isNotBlank() }?.let { "Wind: $it" },
                humidity?.takeIf { it.isNotBlank() }?.let { "Humidity: $it" }
            ).joinToString(", ")

            if (details.isNotEmpty()) {
                weatherDetails.text = details
                weatherDetails.visibility = View.VISIBLE
            } else {
                weatherDetails.visibility = View.GONE
            }
        } else {
            weatherIcon.visibility = View.GONE
            weatherTemp.visibility = View.GONE
            weatherDetails.visibility = View.GONE
        }
    }

    override fun weatherError(errorReason: Int) {
        // Handle weather error
        weatherIcon.visibility = View.GONE
        weatherTemp.visibility = View.GONE
        weatherDetails.visibility = View.GONE
    }
}
