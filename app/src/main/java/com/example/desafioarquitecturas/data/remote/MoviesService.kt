package com.example.desafioarquitecturas.data.remote

import retrofit2.http.GET

interface MoviesService {
    @GET("discover/movie?api_key=9fccbb77c2d087eb822c426d4d6ec411")
    suspend fun getMovies(): MovieResult

}