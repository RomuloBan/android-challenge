package com.example.desafioarquitecturas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafioarquitecturas.data.remote.MoviesService
import com.example.desafioarquitecturas.data.remote.ServerMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel: ViewModel() {

//    var state by mutableStateOf(UiState())
//        private set
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            _state.value = UiState(loading = true)
            _state.value = UiState(
                loading = false,
                movies = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MoviesService::class.java)
                    .getMovies()
                    .results
            )
        }
    }

    fun toggleFavorite(movie: ServerMovie) {
        val movies = _state.value.movies.map {
            if (it.id == movie.id) {
                it.copy(favorite = !it.favorite)
            } else {
                it
            }
        }
        _state.value = _state.value.copy(movies = movies)
    }
    data class UiState(
        val loading: Boolean = false,
        val movies: List<ServerMovie> = emptyList()
    )
}