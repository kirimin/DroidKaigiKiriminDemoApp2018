package me.kirimin.droidkaigikirimindemoapp.presentation.top

import android.view.KeyEvent
import android.view.View
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity
import me.kirimin.droidkaigikirimindemoapp.domain.Language
import me.kirimin.droidkaigikirimindemoapp.domain.Repository
import me.kirimin.droidkaigikirimindemoapp.domain.User
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TopPresenterTest : Spek({

    lateinit var viewMock: TopView
    lateinit var useCaseMock: TopUseCase
    lateinit var presenter: TopPresenter

    describe("TopPresenterTest") {
        beforeEach {
            viewMock = mock()
            useCaseMock = mock()
            presenter = TopPresenter(viewMock, useCaseMock)
        }

        it("onCreate") {
                presenter.onCreate()
                verify(viewMock, times(1)).initView()
        }

        context("on user id submit") {
            beforeEach {
                whenever(useCaseMock.fetchUserInfo(anyString())).thenReturn(Single.never())
                presenter.onCreate()
            }

            fun isError() {
                verify(viewMock, times(1)).showErrorToast(anyString())
                verify(viewMock, never()).setProgressBarVisibility(View.VISIBLE)
                verify(useCaseMock, never()).fetchUserInfo(anyString())
            }

            fun isSuccess() {
                verify(viewMock, never()).showErrorToast(anyString())
                verify(viewMock, times(1)).setParentLayoutVisibility(View.GONE)
                verify(viewMock, times(1)).setProgressBarVisibility(View.VISIBLE)
                verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
                verify(viewMock, never()).setProgressBarVisibility(View.GONE)
                verify(useCaseMock, times(1)).fetchUserInfo("kirimin")
            }

            fun isIgnore() {
                verify(viewMock, never()).showErrorToast(anyString())
                verify(viewMock, never()).setProgressBarVisibility(anyInt())
                verify(viewMock, never()).setParentLayoutVisibility(anyInt())
                verify(useCaseMock, never()).fetchUserInfo(anyString())
            }

            context("submit button click") {
                it("input empty") {
                    presenter.onSubmitButtonClick("")
                    isError()
                }

                it("input text") {
                    presenter.onSubmitButtonClick("kirimin")
                    isSuccess()
                }
            }

            context("edit text key enter") {
                it("input empty") {
                    presenter.onUserIdEditTextKeyListener("", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_UP)
                    isError()
                }

                it("input text and enter key up") {
                    presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_UP)
                    isSuccess()
                }

                it("input text and other key enter") {
                    presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_0, KeyEvent.ACTION_UP)
                    isIgnore()
                }

                it("input text and enter key down") {
                    presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_DOWN)
                    isIgnore()
                }
            }
        }

        context("on fetch user info") {
            beforeEach {
                presenter.onCreate()
            }

            context("success") {

                it("min case") {
                    val userEntity = UserEntity(name = "kirimin", location = null, company = null, blog = null, email = null, avatar_url = null)
                    whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))

                    presenter.onSubmitButtonClick("kirimin")
                    verify(viewMock, times(1)).setUserName("kirimin")
                    verify(viewMock, times(1)).setLocationTextAndVisibility(eq(View.GONE), anyString())
                    verify(viewMock, times(1)).setMailTextAndVisibility(eq(View.GONE), anyString())
                    verify(viewMock, times(1)).setLinkTextAndVisibility(eq(View.GONE), anyString())
                    verify(viewMock, times(1)).setIconVisibility(View.INVISIBLE)
                }

                it("show max view") {
                    val userEntity = UserEntity(name = "kirimin", location = "tokyo, japan", company = "kirimin inc.", blog = "http://kirimin.me", email = "cc@kirimin.me", avatar_url = "http://kirimin.me/face_icon.png")
                    whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))

                    presenter.onSubmitButtonClick("kirimin")
                    verify(viewMock, times(1)).setUserName("kirimin")
                    verify(viewMock, times(1)).setLocationTextAndVisibility(View.VISIBLE, "tokyo, japan")
                    verify(viewMock, times(1)).setMailTextAndVisibility(View.VISIBLE, "cc@kirimin.me")
                    verify(viewMock, times(1)).setLinkTextAndVisibility(View.VISIBLE, "http://kirimin.me")
                    verify(viewMock, times(1)).setIconVisibility(View.VISIBLE)
                    verify(viewMock, times(1)).setIcon("http://kirimin.me/face_icon.png")
                }

                afterEach {
                    verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
                    verify(viewMock, times(1)).setParentLayoutVisibility(View.VISIBLE)
                }
            }

            it("failed") {
                whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.error(Exception("the error")))

                presenter.onSubmitButtonClick("kirimin")
                verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
                verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
                verify(viewMock, times(1)).showErrorToast(anyString())
            }
        }

        it("add repository view") {
            val repositoryEntities = listOf(
                    RepositoryEntity(name = "a"),
                    RepositoryEntity(name = "b"),
                    RepositoryEntity(name = "c")
            )
            whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(UserEntity(name = "kirimin"), repositoryEntities)))

            presenter.onCreate()
            presenter.onSubmitButtonClick("kirimin")
            verify(viewMock, times(1)).addRepositoryView(Repository(RepositoryEntity(name = "a")))
            verify(viewMock, times(1)).addRepositoryView(Repository(RepositoryEntity(name = "b")))
            verify(viewMock, times(1)).addRepositoryView(Repository(RepositoryEntity(name = "c")))
        }

        it("add language view") {
            val a = mock<Language>()
            val b = mock<Language>()
            val c = mock<Language> { on { name } doReturn "c" }
            val languagesMock = listOf(a, b, c)
            val userMock = mock<User> { on { languages } doReturn languagesMock }
            whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(userMock))

            presenter.onCreate()
            presenter.onSubmitButtonClick("kirimin")
            verify(viewMock, times(1)).addLanguageView(a)
            verify(viewMock, times(1)).addLanguageView(b)
            verify(viewMock, times(1)).addLanguageView(c)
        }
    }
})