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
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopPresenter
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopUseCase
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopView
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class TopPresenterTest {

    lateinit var viewMock: TopView
    lateinit var useCaseMock: TopUseCase
    lateinit var presenter: TopPresenter

    @Before
    fun setup() {
        viewMock = mock()
        useCaseMock = mock()
        presenter = TopPresenter(viewMock, useCaseMock)
    }

    @Test
    fun onCreateTest() {
        fun callInitView() {
            verify(viewMock, times(1)).initView()
        }
        presenter.onCreate()
        callInitView()
    }

    @Test
    fun userIdSubmitTest() {
        whenever(useCaseMock.fetchUserInfo(anyString())).thenReturn(Single.never())

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

        fun isSubmitButtonClick() {
            initializeMocks()
            presenter.onSubmitButtonClick("")
            isError()

            initializeMocks()
            presenter.onSubmitButtonClick("kirimin")
            isSuccess()
        }

        fun withEditTextKeyEnter() {
            initializeMocks()
            presenter.onUserIdEditTextKeyListener("", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_UP)
            isError()

            initializeMocks()
            presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_UP)
            isSuccess()

            initializeMocks()
            presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_0, KeyEvent.ACTION_UP)
            isIgnore()

            initializeMocks()
            presenter.onUserIdEditTextKeyListener("kirimin", KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_DOWN)
            isIgnore()
        }

        presenter.onCreate()
        isSubmitButtonClick()
        withEditTextKeyEnter()
    }

    @Test
    fun onFetchUserInfoTest() {

        fun success() {
            fun hideProgressAndShowUserInfoLayout() {
                verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
                verify(viewMock, times(1)).setParentLayoutVisibility(View.VISIBLE)
            }

            fun setUserInfoMinCase() {
                val userEntity = UserEntity(name = "kirimin", location = null, company = null, blog = null, email = null, avatar_url = null)
                whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))
                initializeMocks()

                presenter.onSubmitButtonClick("kirimin")
                hideProgressAndShowUserInfoLayout()
                verify(viewMock, times(1)).setUserName("kirimin")
                verify(viewMock, times(1)).setLocationTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setMailTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setLinkTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setIconVisibility(View.INVISIBLE)
            }

            fun setUserInfoMaxCase() {
                val userEntity = UserEntity(name = "kirimin", location = "tokyo, japan", company = "kirimin inc.", blog = "http://kirimin.me", email = "cc@kirimin.me", avatar_url = "http://kirimin.me/face_icon.png")
                whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))
                initializeMocks()

                presenter.onSubmitButtonClick("kirimin")
                hideProgressAndShowUserInfoLayout()
                verify(viewMock, times(1)).setUserName("kirimin")
                verify(viewMock, times(1)).setLocationTextAndVisibility(View.VISIBLE, "tokyo, japan")
                verify(viewMock, times(1)).setMailTextAndVisibility(View.VISIBLE, "cc@kirimin.me")
                verify(viewMock, times(1)).setLinkTextAndVisibility(View.VISIBLE, "http://kirimin.me")
                verify(viewMock, times(1)).setIconVisibility(View.VISIBLE)
                verify(viewMock, times(1)).setIcon("http://kirimin.me/face_icon.png")

            }

            setUserInfoMinCase()
            setUserInfoMaxCase()
        }

        fun failed() {
            whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.error(Exception("the error")))
            initializeMocks()

            presenter.onSubmitButtonClick("kirimin")
            verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
            verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
            verify(viewMock, times(1)).showErrorToast(anyString())
        }

        presenter.onCreate()
        success()
        failed()
    }

    @Test
    fun addRepositoryViewTest() {
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

    @Test
    fun addLanguageViewTest() {
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

    private fun initializeMocks() {
        clearInvocations(viewMock, useCaseMock)
    }
}
