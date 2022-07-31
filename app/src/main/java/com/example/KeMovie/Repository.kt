package com.example.KeMovie

import com.example.KeMovie.network.ApiService
import com.example.KeMovie.network.ApiServiceDetailMovie

class Repository(private val apiService: ApiService) {

    fun fetchMovies(page:String) = apiService.fetchMovies(page)

}

class RepositoryDetail(private val apiServiceDetailMovie: ApiServiceDetailMovie){

    fun fetchMovie(id: String,api_key: String) = apiServiceDetailMovie.fetchMovie(id ,api_key)
}



