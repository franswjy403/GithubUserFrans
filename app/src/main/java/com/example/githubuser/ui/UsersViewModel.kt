package com.example.githubuser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuser.data.local.entity.User
import com.example.githubuser.data.repository.UsersRepository
import kotlinx.coroutines.launch

class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    fun getUsersList(username: String) = usersRepository.getUser(username)
    fun getUserDetail(username: String) = usersRepository.getOneUser(username)
    fun getFollowers(username: String) = usersRepository.getFollowers(username)
    fun getFollowings(username: String) = usersRepository.getFollowings(username)
    fun getFavoriteUsersList() = usersRepository.getFavoriteUsers()
    fun changeFavorite(user: User) {
        viewModelScope.launch {
            usersRepository.changeFavorite(user)
        }
    }
}