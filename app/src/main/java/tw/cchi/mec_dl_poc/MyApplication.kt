package tw.cchi.mec_dl_poc

import android.app.Application
import android.util.Log
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import tw.cchi.mec_dl_poc.helper.MecHelper
import java.util.*

class MyApplication : Application() {
    companion object {
        @JvmField
        var instance: MyApplication? = null

        /* @JvmStatic
        fun getInstance(): MyApplication {
            return instance as MyApplication
        } */
    }

    var mecHelper = MecHelper()
    var mecConnStatusObservable = Observable()
    var frameResultObservable = Observable()

    override fun onCreate() {
        super.onCreate()
        instance = this

        mecHelper.initUdpStreaming(null, object : MecHelper.OnMecResultListener {
            override fun onConnStatusChange(status: MecConnStatus) {
                mecConnStatusObservable.notifyObservers(status)
            }

            override fun onFrameResult(response: String) {
                Log.i(Constants.TAG, "response=$response")
                frameResultObservable.notifyObservers(response)
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        mecHelper.terminateUdpStreaming()
    }
}
