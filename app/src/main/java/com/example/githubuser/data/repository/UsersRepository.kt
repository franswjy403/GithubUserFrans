package com.example.githubuser.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.githubuser.data.remote.retrofit.ApiService
import com.example.githubuser.data.local.entity.User
import com.example.githubuser.data.local.room.UsersDao

class UsersRepository private constructor(
    private val apiService: ApiService,
    private val usersDao: UsersDao,
) {
    fun getUser(username: String): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getUser(username)
            val users = response.items
            val usersList = users.map { user ->
                val isFavorite = usersDao.isFavorite(user.login)
                User(
                    id = user.id,
                    username = user.login,
                    avatarUrl = user.avatarUrl,
                    isFav = isFavorite
                )
            }
            usersDao.deleteAll()
            usersDao.insertUsers(usersList)
        } catch (e: Exception) {
            Log.d("UsersRepository", "GetUserList: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<User>>> = usersDao.getAllUsers().map {Result.Success(it)}
        emitSource(localData)
    }

    fun getOneUser(username: String): LiveData<Result<User>> = liveData {
        emit(Result.Loading)
        try {
            val user = apiService.getDetailUser(username)
            val isFavorite = usersDao.isFavorite(user.login ?: "")
            val newUser = User(
                id = user.id ?: 1,
                username = user.login ?: "",
                avatarUrl = user.avatarUrl,
                name = user.name,
                isFav = isFavorite,
                followers = user.followers,
                following = user.following,
            )
            usersDao.updateOneUser(newUser)
        } catch (e: Exception) {
            Log.d("UsersRepository", "GetOneUser: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<User>> = usersDao.getOneUser(username).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getFollowers(username: String): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        lateinit var usersList: List<User>
        try {
            val response = apiService.getFollowers(username)
            usersList = response.map { user ->
                User(
                    id = user.id,
                    username = user.login,
                    avatarUrl = user.avatarUrl,
                )
            }
        } catch (e: Exception) {
            Log.d("UsersRepository", "GetUserFollowers: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val followers = MutableLiveData<Result<List<User>>>()
        val localData: LiveData<Result<List<User>>> = followers
        followers.value = Result.Success(usersList)
        emitSource(localData)
    }

    fun getFollowings(username: String): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        var usersList: List<User>? = null
        try {
            val response = apiService.getFollowing(username)
            usersList = response.map { user ->
                User(
                    id = user.id,
                    username = user.login,
                    avatarUrl = user.avatarUrl,
                )
            }
        } catch (e: Exception) {
            Log.d("UsersRepository", "GetUserFollowers: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val followings = MutableLiveData<Result<List<User>>>()
        val localData: LiveData<Result<List<User>>> = followings
        followings.value = Result.Success(usersList!!)
        emitSource(localData)
    }

    fun getFavoriteUsers(): LiveData<Result<List<User>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<User>>> = usersDao.getFavoriteUsers().map {Result.Success(it)}
        emitSource(localData)
    }

    suspend fun changeFavorite(user: User) {
        user.isFav = !user.isFav
        usersDao.updateOneUser(user)
    }

    companion object {
        @Volatile
        private var instance: UsersRepository? = null
        fun getInstance(
            apiService: ApiService,
            usersDao: UsersDao,
        ): UsersRepository = instance ?: synchronized(this) {
            instance ?: UsersRepository(apiService, usersDao)
        }.also { instance = it }
    }
}