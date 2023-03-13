package ru.netology.nmedia.viewmodel

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

//узнать "залогинены" или нет
class AuthViewModel : ViewModel() {
    val data: LiveData<AuthState> = AppAuth.getInstance()
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    //"флаг", вошли в приложение или нет
    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    fun getToken(user: String, pass: String) = viewModelScope.launch{
        try {
            val response = PostsApi.service.updateUser(user, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            AppAuth.getInstance().setAuth(body.id, body.token)


        } catch (e: IOException) {
            AppAuth.getInstance().removeAuth()
            throw NetworkError
        } catch (e: Exception) {
            AppAuth.getInstance().removeAuth()
            throw UnknownError

        }
    }
}