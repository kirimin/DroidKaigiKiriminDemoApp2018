package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import me.kirimin.droidkaigikirimindemoapp.R
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import me.kirimin.droidkaigikirimindemoapp.domain.Repository
import kotlinx.android.synthetic.main.activity_top.*
import me.kirimin.droidkaigikirimindemoapp.domain.Language

interface TopView {

    class TopActivity : AppCompatActivity(), TopView {

        lateinit var presenter: TopPresenter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            presenter = TopPresenter(this)
            presenter.onCreate()
        }

        override fun onDestroy() {
            presenter.onDestroy()
            super.onDestroy()
        }

        override fun initView() {
            setContentView(R.layout.activity_top)
            toolbar.title = "Who on GitHub"
            submitButton.setOnClickListener {
                presenter.onSubmitButtonClick(userIdEditText.text.toString())
            }
            userIdEditText.setOnKeyListener { _, keyCode, event ->
                presenter.onUserIdEditTextKeyListener(userIdEditText.text.toString(), keyCode, event.action)
                false
            }
        }

        override fun showErrorToast(text: String) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }

        override fun setProgressBarVisibility(visibility: Int) {
            progressBar.visibility = visibility
        }

        override fun setParentLayoutVisibility(visibility: Int) {
            parentLayout.visibility = visibility
        }

        override fun hideKeyBoard() {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        override fun setUserName(name: String) {
            nameText.text = name
        }

        override fun setLocationTextAndVisibility(visibility: Int, text: String) {
            locationText.visibility = visibility
            locationText.text = text
        }

        override fun setLinkTextAndVisibility(visibility: Int, text: String) {
            linkText.visibility = visibility
            linkText.text = text
        }

        override fun setMailTextAndVisibility(visibility: Int, text: String) {
            mailText.visibility = visibility
            mailText.text = text
        }

        override fun setIcon(iconUrl: String) {
            Picasso.with(this).load(iconUrl).fit().into(iconImage)
        }

        override fun setIconVisibility(visibility: Int) {
            iconImage.visibility = visibility
        }

        override fun addLanguageView(language: Language) {
            val languageView = layoutInflater.inflate(R.layout.view_language, null)
            languageView.findViewById<TextView>(R.id.userInfoLanguageName).text = language.name
            languageView.findViewById<TextView>(R.id.userInfoLanguageCount).text = language.languageCount
            languageView.findViewById<TextView>(R.id.userInfoLanguageStarCount).text = language.starCount
            languageLayout.addView(languageView)
        }

        override fun addRepositoryView(repository: Repository) {
            val repositoryView = layoutInflater.inflate(R.layout.view_repository, null)
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryName).text = repository.name
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryLanguage).text = repository.language
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryDescription).text = repository.description
            repositoryView.findViewById<TextView>(R.id.userInfoRepositoryStarCount).text = repository.starCount.toString()
            repositoryLayout.addView(repositoryView)
        }
    }

    fun initView()
    fun showErrorToast(text: String)
    fun setProgressBarVisibility(visibility: Int)
    fun setParentLayoutVisibility(visibility: Int)
    fun hideKeyBoard()
    fun setUserName(name: String)
    fun setLocationTextAndVisibility(visibility: Int, text: String)
    fun setMailTextAndVisibility(visibility: Int, text: String)
    fun setLinkTextAndVisibility(visibility: Int, text: String)
    fun setIcon(iconUrl: String)
    fun setIconVisibility(visibility: Int)
    fun addLanguageView(language: Language)
    fun addRepositoryView(repository: Repository)
}