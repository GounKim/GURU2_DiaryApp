package com.example.guru2_diaryapp.diaryView

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 날씨 관련 인터페이스
interface RetrofitService {
    @GET("weather?")
    fun getCurrentWeather(
        @Query("lat") lat : String,
        @Query("lon") lon : String,
        @Query("APPID") APPID : String)
    : Call<JsonObject>
}