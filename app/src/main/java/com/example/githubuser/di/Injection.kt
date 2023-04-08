package com.example.githubuser.di

import android.content.Context
import com.example.githubuser.data.local.room.UsersDatabase
import com.example.githubuser.data.remote.retrofit.ApiConfig
import com.example.githubuser.data.repository.UsersRepository

object Injection {
    fun provideRepository(context: Context): UsersRepository {
        val apiService = ApiConfig.getApiService()
        val database = UsersDatabase.getDatabase(context)
        val dao = database.userDao()
        return UsersRepository.getInstance(apiService, dao)
    }
}