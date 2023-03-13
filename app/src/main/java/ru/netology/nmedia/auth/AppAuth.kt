package ru.netology.nmedia.auth

import android.content.Context
import kotlinx.coroutines.flow.*

//будем обращаться к объекту для сохранения токена и т.д
class AppAuth private constructor(context: Context) {
    //имя файла и ключ настройки Context.MODE_PRIVATE в параметрах
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>

    //при старте приложения
    init {
        //прочитаем id
        val id = prefs.getLong(idKey, 0)
        //прочитаем токен
        val token = prefs.getString(tokenKey, null)
        //если в Preferenc отстутствует id или токен, то чистим все и будет flow, кторый ничего не содержит
        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
               clear()
               apply()
           }
        } else {
            //если все есть, то записываем
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        //синхронизируем данные, кладя по ключам id и token текущие id и токен пользователя
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
    }

    //удаление аутентификации
    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    companion object {
        @Volatile
        private var instance: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }
}

//объект, в котором одновременно и id и token и уже его можно хранить в подписке
data class AuthState(val id: Long = 0, val token: String? = null)