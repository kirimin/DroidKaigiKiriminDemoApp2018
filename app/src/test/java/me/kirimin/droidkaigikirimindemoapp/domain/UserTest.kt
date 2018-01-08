package me.kirimin.droidkaigikirimindemoapp.domain

import com.taroid.knit.should
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity
import org.junit.Before
import org.junit.Test

class UserTest {

    lateinit var user: User
    lateinit var a: RepositoryEntity
    lateinit var b: RepositoryEntity
    lateinit var c: RepositoryEntity
    lateinit var d: RepositoryEntity
    lateinit var e: RepositoryEntity

    @Before
    fun setup() {
        a = RepositoryEntity(name = "a", stargazers_count = 5, fork = false, language = "Java")
        b = RepositoryEntity(name = "b", stargazers_count = 2, fork = false, language = null)
        c = RepositoryEntity(name = "c", stargazers_count = 7, fork = true, language = "Kotlin")
        d = RepositoryEntity(name = "d", stargazers_count = 3, fork = false, language = "Kotlin")
        e = RepositoryEntity(name = "e", stargazers_count = 7, fork = false, language = "PHP")
        user = User(UserEntity(), listOf(a, b, c, d, e))
    }

    @Test
    fun sortedRepositoriesTest() {
        user.sortedRepositories.should be listOf(e, a, d, b, c).map(::Repository)
    }

    @Test
    fun languagesTest() {
        user.languages.should be listOf(
                Language(name = "Kotlin", includeRepositories = listOf(c, d).map(::Repository)),
                Language(name = "Java", includeRepositories = listOf(a).map(::Repository)),
                Language(name = "PHP", includeRepositories = listOf(e).map(::Repository))
        )
    }
}