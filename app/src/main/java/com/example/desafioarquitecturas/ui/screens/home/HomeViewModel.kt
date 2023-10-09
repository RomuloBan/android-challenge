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
import kotlinx.coroutines.launch

class HomeViewModel(val dao: MoviesDao): ViewModel() {

//    var state by mutableStateOf(UiState())
//        private set
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state
    val repository = MoviesRepository()

    init {
        viewModelScope.launch {
            val isDbEmpty = dao.count() == 0
            if (isDbEmpty) {
                _state.value = UiState(loading = true)


            }
            dao.getMovies().collect { movies ->
                _state.value = UiState(
                    loading = false,
                    movies = movies.map { it.toMovie() }
                )
            }
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