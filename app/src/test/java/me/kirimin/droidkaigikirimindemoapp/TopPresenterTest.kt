package me.kirimin.droidkaigikirimindemoapp

import android.view.View
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import me.kirimin.droidkaigikirimindemoapp.data.entity.RepositoryEntity
import me.kirimin.droidkaigikirimindemoapp.data.entity.UserEntity
import me.kirimin.droidkaigikirimindemoapp.domain.User
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopPresenter
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopUseCase
import me.kirimin.droidkaigikirimindemoapp.presentation.top.TopView
import org.junit.Before
import org.junit.Test
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
    fun onSubmitButtonClickTestWithEmptyText() {
        presenter.onCreate()
    }

    @Test
    fun onSubmitButtonClickTest() {
        fun withEmptyText() {
            initializeMocks()

            presenter.onSubmitButtonClick("")
            verify(viewMock, times(1)).showErrorToast(anyString())
            verify(viewMock, never()).setProgressBarVisibility(View.VISIBLE)
            verify(useCaseMock, never()).fetchUserInfo(anyString())
        }

        fun withNotEmptyText() {
            initializeMocks()

            presenter.onSubmitButtonClick("kirimin")
            verify(viewMock, never()).showErrorToast(anyString())
            verify(viewMock, times(1)).setParentLayoutVisibility(View.GONE)
            verify(viewMock, times(1)).setProgressBarVisibility(View.VISIBLE)
            verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
            verify(viewMock, never()).setProgressBarVisibility(View.GONE)
            verify(useCaseMock, times(1)).fetchUserInfo("kirimin")
        }
        whenever(useCaseMock.fetchUserInfo(anyString())).thenReturn(Single.never())

        presenter.onCreate()
        withEmptyText()
        withNotEmptyText()
    }

    @Test
    fun onFetchUserInfoTest() {

        fun success() {
            fun hideProgressAndShowUserInfoLayout() {
                verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
                verify(viewMock, times(1)).setParentLayoutVisibility(View.VISIBLE)
            }

            fun setUserInfoMinCase() {
                initializeMocks()
                val userEntity = UserEntity(name = "kirimin", location = null, company = null, blog = null, email = null, avatar_url = null)
                whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))

                presenter.onSubmitButtonClick("kirimin")
                hideProgressAndShowUserInfoLayout()
                verify(viewMock, times(1)).setUserName("kirimin")
                verify(viewMock, times(1)).setLocationTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setMailTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setLinkTextAndVisibility(eq(View.GONE), anyString())
                verify(viewMock, times(1)).setIconVisibility(View.INVISIBLE)
            }

            fun setUserInfoMaxCase() {
                initializeMocks()
                val userEntity = UserEntity(name = "kirimin", location = "tokyo, japan", company = "kirimin inc.", blog = "http://kirimin.me", email = "cc@kirimin.me", avatar_url = "http://kirimin.me/face_icon.png")
                whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(RepositoryEntity()))))

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
            initializeMocks()
            whenever(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.error(Exception("the error")))

            presenter.onSubmitButtonClick("kirimin")
            verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
            verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
            verify(viewMock, times(1)).showErrorToast(anyString())
        }

        presenter.onCreate()
        success()
        failed()
    }

    private fun initializeMocks() {
        clearInvocations(viewMock, useCaseMock)
    }
}
