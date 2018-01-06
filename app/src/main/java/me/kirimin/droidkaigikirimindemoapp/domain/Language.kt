package me.kirimin.droidkaigikirimindemoapp.domain

class Language(val name: String, private val includeRepositories: List<Repository>) {

    val languageCount = includeRepositories.count().toString()
    val starCount = includeRepositories.map { it.starCount }.sum().toString()
}