package net.ruhamaa.mobile.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.ruhamaa.mobile.Event
import net.ruhamaa.mobile.data.Message
import net.ruhamaa.mobile.data.Result.*
import net.ruhamaa.mobile.data.source.remote.responses.LoginResponse
import net.ruhamaa.mobile.data.toErrorMessage
import net.ruhamaa.mobile.di.KodeinInjector
import net.ruhamaa.mobile.domain.LoginUseCase
import net.ruhamaa.mobile.util.readOnly
import org.kodein.di.erased.instance

class LoginViewModel : ViewModel() {
    private val loginUseCase by KodeinInjector.instance<LoginUseCase>()

    private val _navigateToVerify = MutableLiveData<Event<LoginResponse>>()
    val navigateToVerify = _navigateToVerify.readOnly()

    private val _loading = MutableLiveData(false)
    val loading = _loading.readOnly()

    private val _message = MutableLiveData<Event<Message>>()
    val message = _message.readOnly()

    fun login(phoneNum: String){
        viewModelScope.launch {
            when(val result = loginUseCase(phoneNum)){
                is Success -> _navigateToVerify.value = Event(result.data)
                is Error -> {
                    _message.value = Event(result.toErrorMessage())
                }
            }
        }
    }
}
