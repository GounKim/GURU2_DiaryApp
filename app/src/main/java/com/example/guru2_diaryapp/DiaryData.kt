package com.example.guru2_diaryapp

import android.widget.ImageView

class DiaryData {
    var id:Int = 0
    var reporting_date:Int = 0
    var weather:Int = 0
    var category_name: String = ""
    var content:String = ""
    var imgs:ArrayList<String>? = null

    constructor()

    constructor(id: Int, date: Int, weather: Int, category_name: String, content: String?, imgs:ArrayList<String>?) {
        this.id = id
        this.reporting_date = date
        this.weather = weather
        this.category_name = category_name
        this.content = content.toString()
        this.imgs = imgs
    }

    // 공유 드라이브에 올려둔 날씨 파일 참고
    fun loadWeatherIcon(weather: Int, weatherImg : ImageView) {
        if(weather == 1) {
            weatherImg.setImageResource(R.drawable.sunny)
        } else if(weather == 2) {
            weatherImg.setImageResource(R.drawable.mist)
        } else if(weather == 3) {
            weatherImg.setImageResource(R.drawable.few_cloud)
        } else if(weather == 4) {
            weatherImg.setImageResource(R.drawable.few_cloud)
        } else if(weather == 5) {
            weatherImg.setImageResource(R.drawable.few_cloud)
        } else if(weather == 6) {
            weatherImg.setImageResource(R.drawable.cloud)
        } else if(weather == 7) {
            weatherImg.setImageResource(R.drawable.light_rain)
        } else if(weather == 8) {
            weatherImg.setImageResource(R.drawable.moderate_rain)
        } else if(weather == 9) {
            weatherImg.setImageResource(R.drawable.heavy_rain)
        } else if(weather == 10) {
            weatherImg.setImageResource(R.drawable.thunderstorm)
        } else if(weather == 11) {
            weatherImg.setImageResource(R.drawable.snow)
        } else {
            weatherImg.setImageResource(R.drawable.ic_baseline_refresh_24)
        }
    }

    fun loadCategoryName(category_id: Int) : String {
        if(category_id == 1) {
            return "일상"
        } else if (category_id == 2) {
            return "여행"
        } else if (category_id == 3) {
            return "교환일기"
        } else {
            return "알 수 없음"
        }
    }
}
