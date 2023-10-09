package com.example.desafioarquitecturas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafioarquitecturas.data.Movie
import com.example.desafioarquitecturas.data.MoviesRepository
import com.example.desafioarquitecturas.data.local.MoviesDao
import com.example.desafioarquitecturas.data.local.toLocalMovie
import com.example.desafioarquitecturas.data.local.toMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MoviesRepository): ViewModel() {

//    var state by mutableStateOf(UiState())
//        private set
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            _state.value = UiState(loading = true)
            repository.getMovies()

            repository.movies.collect {
                _state.value = UiState(movies = it, loading = false)
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie.copy(favorite = !movie.favorite))
        }
    }
    data class UiState(
        val loading: Boolean = false,
        val movies: List<Movie> = emptyList()
    )
}