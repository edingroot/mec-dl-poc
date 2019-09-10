package tw.cchi.mec_dl_poc.config

class Constants {
    companion object {
        const val TAG = "mecpoc"

        const val MEC_SERVER_PROTOCOL = "http"
        const val MEC_SERVER_IP = "192.188.2.128"
        const val MEC_SERVER_PORT = 9999
    }
}

enum class MecConnStatus { DISCONNECTED, CONNECTING, CONNECTED, FAILED }
