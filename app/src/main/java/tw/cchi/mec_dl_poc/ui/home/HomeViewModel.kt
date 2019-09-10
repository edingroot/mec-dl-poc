package tw.cchi.mec_dl_poc.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import java.util.*

class HomeViewModel : ViewModel() {
    private val application = MyApplication.instance
    private lateinit var mecConnStatusObserver: Observer
    private lateinit var frameResultObserver: Observer

    private val _title = MutableLiveData<String>().apply { value = "MEC Connection Status" }
    private val _mecConnStatus =
        MutableLiveData<MecConnStatus>().apply { value = MecConnStatus.DISCONNECTED }
    private val _messages = MutableLiveData<String>()

    val title: LiveData<String> = _title
    var mecConnStatus: LiveData<MecConnStatus> = _mecConnStatus
    var messages: LiveData<String> = _messages

    fun initialize() {
        mecConnStatusObserver = Observer { _, arg ->
            _mecConnStatus.value = arg as MecConnStatus
        }

        frameResultObserver = Observer { _, arg ->
            Log.i(Constants.TAG, "response from observer=%s".format(arg as String))
        }

        application!!.frameResultObservable.addObserver(frameResultObserver)
    }

//    fun setMecConnStatus(value: MecConnStatus) {
//        this._mecConnStatus.value = value
//    }
//
//    fun setMessages(value: String) {
//        this._messages.value = value
//    }

    override fun onCleared() {
        super.onCleared()
        application?.frameResultObservable?.deleteObserver(mecConnStatusObserver)
        application?.frameResultObservable?.deleteObserver(frameResultObserver)
    }
}
