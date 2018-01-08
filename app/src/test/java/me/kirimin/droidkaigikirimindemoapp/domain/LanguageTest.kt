package me.kirimin.droidkaigikirimindemoapp.domain

import com.taroid.knit.should
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import org.junit.Before
import org.junit.Test

class LanguageTest {

    lateinit var language: Language

    @Before
    fun setup() {
        language = Language("Kotlin", listOf(
                Repository(RepositoryEntity(name = "Kotlin", stargazers_count = 3)),
                Repository(RepositoryEntity(name = "Kotlin", stargazers_count = 2)),
                Repository(RepositoryEntity(name = "Kotlin", stargazers_count = 10))
        ))
    }

    @Test
    fun languageCountTest() {
        language.languageCount.should be "3"
    }

    @Test
    fun starCountTest() {
        language.starCount.should be "15"
    }
}