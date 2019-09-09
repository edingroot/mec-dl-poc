package tw.cchi.mec_dl_poc.helper

import UdpSocketHelper
import android.os.Handler
import android.os.Message
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

class MecHelper {
    private var streamingInitialized = false
    private var udpServerPort = 0
    private var onFrameResultListener: OnFrameResultListener? = null

    private val httpHelper = HttpHelper()
    private val packetRecvHandler = PacketRecvHandler(WeakReference(this))
    private val udpSocketHelper = UdpSocketHelper(packetRecvHandler)

    fun initUdpStreaming(udpServerPort: Int, onFrameResultListener: OnFrameResultListener) {
        this.udpServerPort = udpServerPort
        this.onFrameResultListener = onFrameResultListener

        this.udpSocketHelper.initUdpSockets(udpServerPort)
        this.streamingInitialized = true
    }

    fun terminateUdpStreaming() {
        this.streamingInitialized = false
        this.udpServerPort = 0
    }

    class PacketRecvHandler(private val outerClass: WeakReference<MecHelper>) : Handler() {
        override fun handleMessage(msg: Message?) {
            outerClass.get()?.onFrameResultListener?.onResult(msg?.obj.toString())
        }
    }

    interface OnFrameResultListener {
        fun onResult(response: String)
    }
}
