package me.kirimin.droidkaigikirimindemoapp.presentation.top

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.repository.UserRepository
import me.kirimin.droidkaigikirimindemoapp.domain.User

class TopUseCase {

    private val repository = UserRepository()

    fun fetchUserInfo(id: String): Single<User> {
        val fetchUser = repository.fetchUser(id)
        val fetchUserRepositories = repository.requestRepository(id, 0)
        return fetchUser.zipWith<List<RepositoryEntity>, User>(fetchUserRepositories, BiFunction(::User))
    }
}