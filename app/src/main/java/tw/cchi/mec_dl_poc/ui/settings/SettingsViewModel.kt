package tw.cchi.mec_dl_poc.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.cchi.mec_dl_poc.MyApplication

class SettingsViewModel : ViewModel() {
    private val prefHelper = MyApplication.instance?.prefHelper

    private val _mecHost = MutableLiveData<String>()
    private val _mecPort = MutableLiveData<Int>()

    val mecHost: LiveData<String> = _mecHost
    val mecPort: LiveData<Int> = _mecPort

    fun initialize() {
        _mecHost.value = prefHelper?.mecServerHost
        _mecPort.value = prefHelper?.mecServerPort
    }

    fun savePreferences() {
        prefHelper?.mecServerHost = mecHost.value!!
        prefHelper?.mecServerPort = mecPort.value!!
    }

    fun setMecHost(value: String) {
        _mecHost.value = value
    }

    fun setMecPort(value: Int) {
        _mecPort.value = value
    }
}
