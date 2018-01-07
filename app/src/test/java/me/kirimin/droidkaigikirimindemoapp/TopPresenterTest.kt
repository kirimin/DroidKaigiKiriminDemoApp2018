package me.kirimin.droidkaigikirimindemoapp

import android.view.View
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
import org.mockito.Mockito
import org.mockito.Mockito.*

class TopPresenterTest {

    lateinit var viewMock: TopView
    lateinit var useCaseMock: TopUseCase
    lateinit var presenter: TopPresenter

    @Before
    fun setup() {
        viewMock = mock(TopView::class.java)
        useCaseMock = mock(TopUseCase::class.java)
        presenter = TopPresenter(viewMock, useCaseMock)
    }

    @Test
    fun onCreateTest() {
        presenter.onCreate()
        // initView()メソッドが呼ばれている事を確認
        verify(viewMock, Mockito.times(1)).initView()
    }

    @Test
    fun onSubmitButtonClickTestWithEmptyText() {
        presenter.onCreate()
        presenter.onSubmitButtonClick("")
        // 入力された文字列が空の場合はエラーが表示され処理が終了することを確認
        verify(viewMock, times(1)).showErrorToast("validation error")
        verify(viewMock, never()).setProgressBarVisibility(View.VISIBLE)
        verify(useCaseMock, never()).fetchUserInfo(anyString())
    }

    @Test
    fun onSubmitButtonClickTestWithText() {
        Mockito.`when`(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.never())

        presenter.onCreate()
        presenter.onSubmitButtonClick("kirimin")

        // 入力された文字列が空の場合はエラーが表示されない事を確認
        verify(viewMock, never()).showErrorToast("validation error")

        // 入力された文字列を使用してデータの取得が行われることを確認
        verify(viewMock, times(1)).setParentLayoutVisibility(View.GONE)
        verify(viewMock, times(1)).setProgressBarVisibility(View.VISIBLE)
        verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
        verify(viewMock, never()).setProgressBarVisibility(View.GONE)
        verify(useCaseMock, times(1)).fetchUserInfo("kirimin")
    }

    @Test
    fun onFetchUserInfoSuccessMinCaseTest() {
        // UseCaseが返す値をモック
        val userEntity = UserEntity(name = "kirimin", location = null, company = null, blog = null, email = null, avatar_url = null)
        val repoEntity = RepositoryEntity()
        Mockito.`when`(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(repoEntity))))

        presenter.onCreate()
        presenter.onSubmitButtonClick("kirimin")

        verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
        verify(viewMock, times(1)).setParentLayoutVisibility(View.VISIBLE)

        verify(viewMock, times(1)).setUserName("kirimin")
        verify(viewMock, times(1)).setLocationTextAndVisibility(eq(View.GONE), anyString())
        verify(viewMock, times(1)).setMailTextAndVisibility(eq(View.GONE), anyString())
        verify(viewMock, times(1)).setLinkTextAndVisibility(eq(View.GONE), anyString())
        verify(viewMock, times(1)).setIconVisibility(View.INVISIBLE)
    }

    @Test
    fun onFetchUserInfoMaxCaseTest() {
        // UseCaseが返す値をモック
        val userEntity = UserEntity(name = "kirimin", location = "tokyo, japan", company = "kirimin inc.", blog = "http://kirimin.me", email = "cc@kirimin.me", avatar_url = "http://kirimin.me/face_icon.png")
        val repoEntity = RepositoryEntity()
        Mockito.`when`(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.just(User(userEntity, listOf(repoEntity))))

        presenter.onCreate()
        presenter.onSubmitButtonClick("kirimin")

        verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
        verify(viewMock, times(1)).setParentLayoutVisibility(View.VISIBLE)

        verify(viewMock, times(1)).setUserName("kirimin")
        verify(viewMock, times(1)).setLocationTextAndVisibility(View.VISIBLE, "tokyo, japan")
        verify(viewMock, times(1)).setMailTextAndVisibility(View.VISIBLE, "cc@kirimin.me")
        verify(viewMock, times(1)).setLinkTextAndVisibility(View.VISIBLE, "http://kirimin.me")
        verify(viewMock, times(1)).setIconVisibility(View.VISIBLE)
        verify(viewMock, times(1)).setIcon("http://kirimin.me/face_icon.png")
    }

    @Test
    fun onFetchUserInfoFailedTest() {
        Mockito.`when`(useCaseMock.fetchUserInfo("kirimin")).thenReturn(Single.error(Exception("the error")))

        presenter.onCreate()
        presenter.onSubmitButtonClick("kirimin")

        verify(viewMock, times(1)).setProgressBarVisibility(View.GONE)
        verify(viewMock, never()).setParentLayoutVisibility(View.VISIBLE)
        verify(viewMock, Mockito.times(1)).showErrorToast("network error")
    }

}
