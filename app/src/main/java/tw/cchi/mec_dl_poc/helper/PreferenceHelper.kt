package tw.cchi.mec_dl_poc.helper

import android.content.Context
import tw.cchi.mec_dl_poc.config.Constants

class PreferenceHelper(context: Context) {
    private val PREF_NAME = "mec_poc_pref"
    private val mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val KEY_MEC_SERVER_HOST = "KEY_MEC_SERVER_HOST"
    private val KEY_MEC_SERVER_PORT = "KEY_MEC_SERVER_PORT"

    var mecServerHost: String = Constants.DEFAULT_MEC_SERVER_HOST
        get() =
            mPrefs.getString(KEY_MEC_SERVER_HOST, Constants.DEFAULT_MEC_SERVER_HOST) ?:
            Constants.DEFAULT_MEC_SERVER_HOST
        set(value) {
            mPrefs.edit().putString(KEY_MEC_SERVER_HOST, value).commit()
            field = value
        }

    var mecServerPort: Int = Constants.DEFAULT_MEC_SERVER_PORT
        get() = mPrefs.getInt(KEY_MEC_SERVER_PORT, Constants.DEFAULT_MEC_SERVER_PORT)
        set(value) {
            mPrefs.edit().putInt(KEY_MEC_SERVER_PORT, value).commit()
            field = value
        }
}
