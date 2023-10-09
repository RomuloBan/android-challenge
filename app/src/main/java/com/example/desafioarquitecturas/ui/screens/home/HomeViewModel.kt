package com.example.desafioarquitecturas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafioarquitecturas.data.Movie
import com.example.desafioarquitecturas.data.local.MoviesDao
import com.example.desafioarquitecturas.data.local.toLocalMovie
import com.example.desafioarquitecturas.data.local.toMovie
import com.example.desafioarquitecturas.data.remote.MoviesService
import com.example.desafioarquitecturas.data.remote.toLocalMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(val dao: MoviesDao): ViewModel() {

//    var state by mutableStateOf(UiState())
//        private set
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            val isDbEmpty = dao.count() == 0
            if (isDbEmpty) {
                _state.value = UiState(loading = true)
                dao.insertAll(
                    Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(MoviesService::class.java)
                        .getMovies()
                        .results
                        .map { it.toLocalMovie() }
                )
            }
            _state.value = UiState(
                loading = false,
                movies = dao.getMovies().map { it.toMovie() }
            )
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            dao.updateMovie(movie.copy(favorite = !movie.favorite).toLocalMovie())
        }
    }
    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie> = emptyList()
    )
}