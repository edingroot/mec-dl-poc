package tw.cchi.mec_dl_poc

import android.app.Application
import tw.cchi.mec_dl_poc.helper.MecHelper

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

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
