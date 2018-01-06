package me.kirimin.droidkaigikirimindemoapp.domain

import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity

data class Repository(private val entity: RepositoryEntity) {

    val name = entity.name
    val language = entity.language
    val starCount = entity.stargazers_count
    val description = entity.description
    val isFork = entity.fork
}