package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.view.View
import android.widget.EditText
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_top.*
import me.kirimin.droidkaigikirimindemoapp.domain.Repository
import me.kirimin.droidkaigikirimindemoapp.domain.User

class TopPresenter(val view: TopActivity) {

    val useCase = TopUseCase()
    lateinit var disposables: CompositeDisposable
    var user: User? = null

    fun onCreate() {
        disposables = CompositeDisposable()
    }

    fun onDestroy() {
        disposables.clear()
    }

    fun getUserInfo(userIdEditText: EditText) {
        val id = userIdEditText.text.toString()
        if (id.isEmpty()) {
            Toast.makeText(view, "validation error", Toast.LENGTH_SHORT).show()
            return
        }
        view.progressBar.visibility = View.VISIBLE
        useCase.fetchUserInfo(id).subscribe({ user ->
            this.user = user
            view.progressBar.visibility = View.GONE
            view.setUserInfo(user)
            view.setLanguages(getLanguages(user.repositories))
            view.setRepositories(sortRepositories(user.repositories))
        }, {
            view.progressBar.visibility = View.GONE
            Toast.makeText(view, "network error", Toast.LENGTH_SHORT).show()
        }).also { disposables.add(it) }
    }

    /**
     * リポジトリを言語ごとに言語名とリポジトリリストのPairにまとめたListを返す
     */
    fun getLanguages(repositories: List<Repository>): List<Pair<String, List<Repository>>> {
        return repositories
                .filter { it.language != null }
                .groupBy { it.language!! }
                .toList().sortedByDescending { language -> language.second.count() }
    }

    /**
     * リポジトリをスターの数とforkか否かでソートして返す
     */
    fun sortRepositories(repositories: List<Repository>): List<Repository> {
        return repositories
                .sortedByDescending { repo -> repo.starCount }
                .sortedBy { repo -> repo.isFork }
    }
}