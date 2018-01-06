package me.kirimin.droidkaigikirimindemoapp.data.network

import io.reactivex.Single
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {

    @GET("users/{id}")
    fun fetchUser(@Path("id") id: String): Single<UserEntity>

    @GET("users/{id}/repos")
    fun fetchRepositories(@Path("id") id: String,
                          @Query("page") page: Int,
                          @Query("per_page") perPage: Int = 100): Single<List<RepositoryEntity>>
}