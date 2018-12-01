package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.view.KeyEvent
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import me.kirimin.droidkaigikirimindemoapp.domain.User

class TopPresenter(val view: TopView, val useCase: TopUseCase) {

    lateinit var disposables: CompositeDisposable
    var user: User? = null

    fun onCreate() {
        disposables = CompositeDisposable()
        view.initView()
    }

    fun onDestroy() {
        disposables.clear()
    }

    fun onSubmitButtonClick(text: String) {
        getUserInfo(text)
    }

    fun onUserIdEditTextKeyListener(text: String, keyCode: Int, action: Int) {
        if (KeyEvent.KEYCODE_ENTER == keyCode && action == KeyEvent.ACTION_UP) {
            getUserInfo(text)
        }
    }

    private fun getUserInfo(id: String) {
        if (id.isEmpty()) {
            view.showErrorToast("validation error")
            return
        }
        view.hideKeyBoard()
        view.setProgressBarVisibility(View.VISIBLE)
        view.setParentLayoutVisibility(View.GONE)
        useCase.fetchUserInfo(id).subscribe({ user ->
            this.user = user
            view.setProgressBarVisibility(View.GONE)
            view.setParentLayoutVisibility(View.VISIBLE)
            view.setUserName(user.name)
            if (user.location.isNullOrEmpty()) {
                view.setLocationTextAndVisibility(View.GONE, "")
            } else {
                view.setLocationTextAndVisibility(View.VISIBLE, user.location)
            }
            if (user.mail.isNullOrEmpty()) {
                view.setMailTextAndVisibility(View.GONE, "")
            } else {
                view.setMailTextAndVisibility(View.VISIBLE, user.mail)
            }
            if (user.link.isNullOrEmpty()) {
                view.setLinkTextAndVisibility(View.GONE, "")
            } else {
                view.setLinkTextAndVisibility(View.VISIBLE, user.link)
            }
            if (user.iconUrl != null) {
                view.setIconVisibility(View.VISIBLE)
                view.setIcon(user.iconUrl)
            } else {
                view.setIconVisibility(View.INVISIBLE)
            }

            user.languages.forEach {
                view.addLanguageView(it)
            }
            user.sortedRepositories.forEach {
                view.addRepositoryView(it)
            }
        }, {
            view.setProgressBarVisibility(View.GONE)
            view.showErrorToast("network error")
        }).also { disposables.add(it) }
    }
}