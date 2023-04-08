package com.example.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubuser.data.local.entity.User

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUsers(users: List<User>)

    @Update
    suspend fun updateOneUser(user: User)

    @Query("DELETE FROM users WHERE NOT is_fav")
    suspend fun deleteAll()

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getOneUser(username: String): LiveData<User>

    @Query("SELECT * FROM users WHERE is_fav ORDER BY id ASC")
    fun getFavoriteUsers(): LiveData<List<User>>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE username = :username AND is_fav)")
    suspend fun isFavorite(username: String): Boolean

    @Query("UPDATE users SET is_fav = :status WHERE id = :id")
    suspend fun changeFavorite(id: Int, status: Boolean)
}