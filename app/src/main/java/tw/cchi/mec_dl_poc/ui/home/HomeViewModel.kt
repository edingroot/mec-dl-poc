package tw.cchi.mec_dl_poc.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import java.util.*

class HomeViewModel : ViewModel() {
    private val TAG = Constants.TAG + "/HomeViewModel"
    private val application = MyApplication.instance
    private val mecHelper = application?.mecHelper

    // MutableLiveData
    private val _title = MutableLiveData<String>().apply { value = "MEC Connection Status" }
    private val _mecConnStatus =
        MutableLiveData<MecConnStatus>().apply { value = MecConnStatus.DISCONNECTED }
    private val _messages = MutableLiveData<String>().apply { value = "" }

    // LiveData for access
    val title: LiveData<String> = _title
    var mecConnStatus: LiveData<MecConnStatus> = _mecConnStatus
    var messages: LiveData<String> = _messages

    // Observers
    private val mecConnStatusObserver = Observer { _, arg ->
        _mecConnStatus.postValue(arg as MecConnStatus)
    }
    private val mecStatusMsgObserver = Observer { _, arg ->
        CoroutineScope(Dispatchers.Main).launch {
            var value = ""
            if (_messages.value != "")
                value = _messages.value + "\n"
            _messages.value = value + arg
        }
    }
    private val frameResultObserver = Observer { _, arg ->
        Log.i(TAG, "response from observer=%s".format(arg as String))
    }

    fun initialize() {
        application?.mecConnStatusObservable?.addObserver(mecConnStatusObserver)
        application?.mecStatusMsgObservable?.addObserver(mecStatusMsgObserver)
        application?.frameResultObservable?.addObserver(frameResultObserver)

        // Fill initial values
        _mecConnStatus.value = mecHelper?.connectStatus
        _messages.value = mecHelper?.statusMessage
    }

    override fun onCleared() {
        super.onCleared()
        application?.mecConnStatusObservable?.deleteObserver(mecConnStatusObserver)
        application?.mecStatusMsgObservable?.deleteObserver(mecStatusMsgObserver)
        application?.frameResultObservable?.deleteObserver(frameResultObserver)
    }

//    fun setMecConnStatus(value: MecConnStatus) {
//        this._mecConnStatus.value = value
//    }
//
//    fun setMessages(value: String) {
//        this._messages.value = value
//    }
}
