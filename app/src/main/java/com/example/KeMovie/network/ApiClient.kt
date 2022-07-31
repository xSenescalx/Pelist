package com.example.KeMovie.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object ApiClient {

    private val BASE_URL = "https://api.themoviedb.org/3/"

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiServiceDetailMovie: ApiServiceDetailMovie by lazy {
        retrofit.create(ApiServiceDetailMovie::class.java)
    }
}

interface ApiService{

    @GET("movie/popular?api_key=cd8a36fbb06bbad9356d49a1a8e6bc3a")
    fun fetchMovies(@Query("page") page:String): Call<MovieResponse>
}

interface ApiServiceDetailMovie{

    @GET("movie/{id}")
    fun fetchMovie(@Path("id") id:String, @Query("api_key") api_key:String): Call<DetailMovieResponse>
}