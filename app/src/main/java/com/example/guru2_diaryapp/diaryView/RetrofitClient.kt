package com.example.guru2_diaryapp.diaryView

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 날씨 관련 클래스
class RetrofitClient {
    companion object {
        private val retrofitClient : RetrofitClient =
            RetrofitClient()

        fun getInstance() : RetrofitClient {
            return retrofitClient
        }
    }

    fun buildRetrofit() : RetrofitService {
        val retrofit : Retrofit? = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
                // http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service : RetrofitService = retrofit!!.create(RetrofitService::class.java)
        return service
    }
}