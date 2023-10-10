package com.example.desafioarquitecturas.data

import com.example.desafioarquitecturas.data.local.LocalDataSource
import com.example.desafioarquitecturas.data.remote.RemoteDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verifyBlocking

class MoviesRepositoryTest {

    @Test
    fun `When DB is empty, server is called`() {
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 0
        }
        val remoteDataSource = mock<RemoteDataSource>()
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        runBlocking { repository.getMovies() }

        verifyBlocking(remoteDataSource) { getMovies() }
    }

    @Test
    fun `When DB is not empty, server is not called`() {
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 1
        }
        val remoteDataSource = mock<RemoteDataSource>()
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        runBlocking { repository.getMovies() }

        verifyBlocking(remoteDataSource, never()) { getMovies() }
    }

    @Test
    fun `When DB is empty, movies are inserted`() {
        val movieList = listOf(
            Movie(1, "Title 1", "Overview 1", "Poster 1", false),
            Movie(2, "Title 2", "Overview 2", "Poster 2", false),
            Movie(3, "Title 3", "Overview 3", "Poster 3", false),
        )
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 0
        }
        val remoteDataSource = mock<RemoteDataSource>() {
            onBlocking { getMovies() } doReturn movieList
        }
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        runBlocking { repository.getMovies() }

        verifyBlocking(localDataSource) {
            insertAll(movieList)
        }
    }

    @Test
    fun `When DB is not empty, movies are recovered from DB`() {
        val localMovies = listOf(Movie(1, "Title 1", "Overview 1", "Poster 1", false))
        val remoteMovies = listOf(Movie(2, "Title 2", "Overview 2", "Poster 2", false))
        val localDataSource = mock<LocalDataSource>() {
            onBlocking { count() } doReturn 1
            onBlocking { movies } doReturn flowOf(localMovies)
        }
        val remoteDataSource = mock<RemoteDataSource>() {
            onBlocking { getMovies() } doReturn remoteMovies
        }
        val repository = MoviesRepository(localDataSource, remoteDataSource)

        val result = runBlocking {
            repository.getMovies()
            repository.movies.first()
        }

        assertEquals(result, localMovies)
    }
}