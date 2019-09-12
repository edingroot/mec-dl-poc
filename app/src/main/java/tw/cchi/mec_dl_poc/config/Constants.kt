package tw.cchi.mec_dl_poc.config

class Constants {
    companion object {
        const val TAG = "mecpoc"

        const val MEC_SERVER_PROTOCOL = "http"
        const val DEFAULT_MEC_SERVER_HOST = "192.188.2.116"
        const val DEFAULT_MEC_SERVER_PORT = 9999
        const val LOCAL_UDP_PORT = 6666

        // UDP video streaming
        const val CHUNK_PACK_SIZE = 1000
        const val CHUNK_MAX_PACK = 500
        const val PIC_FILE_NAME = "pic.jpg"

        const val REQUEST_CAMERA_PERMISSION = 1
    }
}

enum class MecConnStatus { DISCONNECTED, CONNECTING, CONNECTED, FAILED }
