package tw.cchi.mec_dl_poc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MecViewModel : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = "MEC Connection Status"
    }
    private val _mecStatus = MutableLiveData<String>().apply {
        value = "MEC Connection Status"
    }

    val title: LiveData<String> = _title
    val mecStatus: LiveData<String> = _mecStatus

    fun setMecStatus(str: String) {
        this._mecStatus.value = str
    }
}
