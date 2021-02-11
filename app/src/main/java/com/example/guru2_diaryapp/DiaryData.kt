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
    // db에서 날씨 불러올때 사용
    fun setWeatherDesc(weather: Int) : String {
        if(weather == 1) {
            return "clear sky"
        } else if(weather == 2) {
            return "mist"
        } else if(weather == 3) {
            return "few clouds"
        } else if(weather == 4) {
            return "broken clouds"
        } else if(weather == 5) {
            return "scattered clouds"
        } else if(weather == 6) {
            return "overcast clouds"
        } else if(weather == 7) {
            return "light rain"
        } else if(weather == 8) {
            return "moderate rain"
        } else if(weather == 9) {
            return "heavy intensity rain"
        } else if(weather == 10) {
            return "thunderstorm"
        } else if(weather == 11) {
            return "snow"
        } else {
            return ""
        }
    }

    fun setWeatherIcon(weather: Int, weatherImg : ImageView) {
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

    // db에 날씨 저장할 때 사용
    fun saveWeatherID(descWeather : String) : Int {
        if (descWeather == "clear sky") { // 맑은 하늘
            return 1
        } else if (descWeather == "mist") { // 안개
            return 2
        } else if (descWeather == "few clouds") { // 조금 흐림
            return 3
        } else if (descWeather == "broken clouds") { // 흩어진 구름
            return 4
        } else if (descWeather == "scattered clouds") { // 흩어진 구름
            return 5
        } else if (descWeather == "overcast clouds") { // 흐린 구름, 많은 구름
            return 6
        }else if (descWeather == "light rain") { // 약한 비
            return 7
        } else if (descWeather == "moderate rain") { // 비 - 보통
            return 8
        } else if (descWeather == "heavy intensity rain") { // 강한 비
            return 9
        } else if (descWeather == "thunderstorm") { // 천둥번개
            return 10
        }else if (descWeather == "snow"){ // 눈
            return 11
        } else {
            return 12
        }
    }

    // db에서 카테고리 불러올때 사용
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

    // db에 카테고리 저장할 때 사용
    fun saveCategoryID(category : String) : Int {
        if(category == "일상") {
            return 1
        } else if (category == "여행") {
            return 2
        } else if (category == "교환일기") {
            return 3
        } else {
            return 0
        }
    }
}
