package com.example.desafioarquitecturas.data

import com.example.desafioarquitecturas.data.local.LocalDataSource
import com.example.desafioarquitecturas.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class MoviesRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    val movies: Flow<List<Movie>> = localDataSource.movies

    suspend fun getMovies() {
        if (localDataSource.count() == 0) {
            localDataSource.insertAll(remoteDataSource.getMovies())
        }
    }

    suspend fun updateMovie(movie: Movie) {
        localDataSource.updateMovie(movie)
    }
}