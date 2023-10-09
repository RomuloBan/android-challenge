package com.example.desafioarquitecturas.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Update

@Database(entities = [LocalMovie::class], version = 1)
abstract class MoviesDatabase {
    abstract fun moviesDao(): MoviesDao
}

@Dao
interface MoviesDao {
    @Query("SELECT * FROM LocalMovie")
    suspend fun getMovies(): List<LocalMovie>
    @Update
    suspend fun updateMovie(movie: LocalMovie)
}

@Entity
data class LocalMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val favorite: Boolean = false
)