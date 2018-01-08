package me.kirimin.droidkaigikirimindemoapp.domain

import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity

data class User(private val entity: UserEntity, private val repos: List<RepositoryEntity>) {

    val id = entity.id
    val name = entity.name
    val location = entity.location
    val mail = entity.email
    val link = entity.blog
    val iconUrl = entity.avatar_url

    val repositories = repos.map(::Repository)

    val languages = repositories
            .filter { it.language != null }
            .groupBy { it.language!! }
            .toList().sortedByDescending { language -> language.second.count() }
            .map { Language(it.first, it.second) }

    val sortedRepositories = repositories
            .sortedByDescending(Repository::starCount)
            .sortedBy(Repository::isFork)
}