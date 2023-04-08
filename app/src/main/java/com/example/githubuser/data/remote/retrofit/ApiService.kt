package com.example.githubuser.data.remote.retrofit

import com.example.githubuser.data.remote.response.GithubResponse
import com.example.githubuser.data.remote.response.GithubUser
import com.example.githubuser.data.remote.response.GithubUserDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getUser(
        @Query("q") q: String
    ): GithubResponse

    @GET("users/{username}")
    suspend fun getDetailUser(
        @Path("username") username: String
    ): GithubUserDetailResponse

    @GET("users/{username}/followers")
    suspend fun getFollowers(
        @Path("username") username: String
    ): List<GithubUser>

    @GET("users/{username}/following")
    suspend fun getFollowing(
        @Path("username") username: String
    ): List<GithubUser>
}