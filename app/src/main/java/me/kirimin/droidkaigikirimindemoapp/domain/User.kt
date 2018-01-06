package me.kirimin.droidkaigikirimindemoapp.domain

import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity

class User(private val entity: UserEntity, private val repos: List<RepositoryEntity>) {

    val id = entity.id
    val name = entity.name
    val location = entity.location
    val mail = entity.email
    val link = entity.blog
    val iconUrl = entity.avatar_url

    val repositories = repos.map(::Repository)
}