package tw.cchi.mec_dl_poc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.cchi.mec_dl_poc.config.MecConnStatus

class MecViewModel : ViewModel() {
    private val _title = MutableLiveData<String>().apply { value = "MEC Connection Status" }
    private val _mecConnStatus =
        MutableLiveData<MecConnStatus>().apply { value = MecConnStatus.CONNECTED }

    val title: LiveData<String> = _title
    var mecConnStatus: LiveData<MecConnStatus> = _mecConnStatus

    fun setMecConnStatus(value: MecConnStatus) {
        this._mecConnStatus.value = value
    }
}
