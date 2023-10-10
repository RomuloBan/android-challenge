package com.example.desafioarquitecturas.data

import com.example.desafioarquitecturas.data.local.LocalDataSource
import com.example.desafioarquitecturas.data.remote.RemoteDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
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
}