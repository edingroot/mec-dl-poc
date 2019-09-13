package tw.cchi.mec_dl_poc.ui.camera

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONException
import org.json.JSONObject
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.config.Constants
import java.util.*

class CameraViewModel : ViewModel() {
    private val TAG = Constants.TAG + "/CameraViewModel"
    private val application = MyApplication.instance

    // MutableLiveData
    private val _frameResult = MutableLiveData<String>()

    // LiveData for access
    val frameResult: LiveData<String> = _frameResult

    private val frameResultObserver = Observer { _, arg ->
        Log.i(TAG, "response from observer=%s".format(arg as String))

        try {
            val json = JSONObject(arg.toString())
            _frameResult.value = json.getString("rawdata")
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing json of frame result")
        }
    }

    fun initialize() {
        application?.frameResultObservable?.addObserver(frameResultObserver)
    }

    override fun onCleared() {
        super.onCleared()
        application?.frameResultObservable?.deleteObserver(frameResultObserver)
    }
}
