package tw.cchi.mec_dl_poc

import android.app.Application
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import tw.cchi.mec_dl_poc.helper.MecHelper
import tw.cchi.mec_dl_poc.helper.PreferenceHelper
import tw.cchi.mec_dl_poc.util.Observable

class MyApplication : Application() {
    companion object {
        @JvmField
        var instance: MyApplication? = null

        /* @JvmStatic
        fun getInstance(): MyApplication {
            return instance as MyApplication
        } */
    }

    lateinit var prefHelper: PreferenceHelper
    lateinit var mecHelper: MecHelper
    var mecConnStatusObservable = Observable()
    var mecStatusMsgObservable = Observable()
    var frameResultObservable = Observable()

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefHelper = PreferenceHelper(this)
        mecHelper = MecHelper(prefHelper)
    }

    fun connectMecServer() {
        mecHelper.initUdpStreaming(Constants.LOCAL_UDP_PORT, object : MecHelper.OnMecResultListener {
            override fun onConnStatusChange(status: MecConnStatus) {
                mecConnStatusObservable.notifyObservers(status)
            }

            override fun onStatusMessage(message: String) {
                mecStatusMsgObservable.notifyObservers(message)
            }

            override fun onFrameResult(response: String) {
                frameResultObservable.notifyObservers(response)
            }
        })
    }

    fun reconnectMecServer() {
        mecHelper.terminateUdpStreaming()
        connectMecServer()
    }

    override fun onTerminate() {
        super.onTerminate()
        mecHelper.terminateUdpStreaming()
    }
}
