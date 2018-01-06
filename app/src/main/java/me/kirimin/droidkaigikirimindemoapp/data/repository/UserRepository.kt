package me.kirimin.droidkaigikirimindemoapp.data.repository

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity
import me.kirimin.droidkaigikirimindemoapp.data.network.GitHubService
import me.kirimin.droidkaigikirimindemoapp.data.network.RetrofitClient

class UserRepository {

    fun fetchUser(id: String): Single<UserEntity> {
        return RetrofitClient.default().build().create(GitHubService::class.java).fetchUser(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun requestRepository(id: String, page: Int): Single<List<RepositoryEntity>> {
        val retrofit = RetrofitClient.default().build().create(GitHubService::class.java)
        return retrofit.fetchRepositories(id = id, page = page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

}