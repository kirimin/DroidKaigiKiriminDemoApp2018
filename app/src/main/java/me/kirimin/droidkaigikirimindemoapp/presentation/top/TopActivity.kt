package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.squareup.picasso.Picasso
import me.kirimin.droidkaigikirimindemoapp.R
import me.kirimin.droidkaigikirimindemoapp.domain.User
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import me.kirimin.droidkaigikirimindemoapp.domain.Repository
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : AppCompatActivity() {

    lateinit var presenter: TopPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        presenter = TopPresenter(this)
        presenter.onCreate()
        toolbar.title = "Who on GitHub"
        submitButton.setOnClickListener {
            hideKeyBoard()
            parentLayout.visibility = View.GONE
            presenter.getUserInfo(userIdEditText)
        }
        userIdEditText.setOnKeyListener { view, keyCode, event ->
            if (KeyEvent.KEYCODE_ENTER == keyCode && event.action == KeyEvent.ACTION_UP) {
                hideKeyBoard()
                parentLayout.visibility = View.GONE
                presenter.getUserInfo(userIdEditText)
            }
            false
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    fun setUserInfo(user: User) {
        parentLayout.visibility = View.VISIBLE
        nameText.text = user.name
        if (user.location.isNullOrEmpty()) {
            locationText.visibility = View.GONE
        } else {
            locationText.visibility = View.VISIBLE
            locationText.text = user.location
        }
        if (user.mail.isNullOrEmpty()) {
            mailText.visibility = View.GONE
        } else {
            mailText.visibility = View.VISIBLE
            mailText.text = user.mail
        }
        if (user.link.isNullOrEmpty()) {
            linkText.visibility = View.GONE
        } else {
            linkText.visibility = View.VISIBLE
            linkText.text = user.link
        }
        Picasso.with(this).load(user.iconUrl).fit().into(iconImage)
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun setLanguages(languages: List<Pair<String, List<Repository>>>) {
        languages.forEach {
            val languageView = layoutInflater.inflate(R.layout.view_language, null)
            languageView.findViewById<TextView>(R.id.userInfoLanguageName).text = it.first
            languageView.findViewById<TextView>(R.id.userInfoLanguageCount).text = it.second.count().toString()
            languageView.findViewById<TextView>(R.id.userInfoLanguageStarCount).text = it.second.map { it.starCount }.sum().toString()
            languageLayout.addView(languageView)
        }
    }

    fun setRepositories(repositories: List<Repository>) {
        repositories.forEach {
            val repositoryView = layoutInflater.inflate(R.layout.view_repository, null)
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryName).text = it.name
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryLanguage).text = it.language
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryDescription).text = it.description
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryStarCount).text = it.starCount.toString()
            repositoryLayout.addView(repositoryView)
        }
    }
}
