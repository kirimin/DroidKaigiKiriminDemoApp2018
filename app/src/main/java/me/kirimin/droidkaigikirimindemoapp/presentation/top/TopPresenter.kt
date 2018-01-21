package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_top.*
import me.kirimin.droidkaigikirimindemoapp.R
import me.kirimin.droidkaigikirimindemoapp.domain.Repository

class TopPresenter(val view: TopActivity) {

    val useCase = TopUseCase()
    lateinit var disposables: CompositeDisposable

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
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        useCase.fetchUserInfo(id).subscribe({ user ->
            progressBar.visibility = View.GONE
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