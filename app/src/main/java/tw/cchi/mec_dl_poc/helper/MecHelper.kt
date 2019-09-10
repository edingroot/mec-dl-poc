package tw.cchi.mec_dl_poc.helper

import UdpSocketHelper
import android.os.Handler
import android.os.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MecHelper {
    var streamingInitialized = false
        private set
    private var udpServerPort = 0
    private var onFrameResultListener: OnFrameResultListener? = null

    // Dispatches execution into Android main thread
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
    // A pool of shared threads as coroutine dispatcher
    private val bgDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val httpHelper = HttpHelper()
    private val packetRecvHandler = PacketRecvHandler(WeakReference(this))
    private val udpSocketHelper = UdpSocketHelper(packetRecvHandler)

    fun initUdpStreaming(udpServerPort: Int?, onFrameResultListener: OnFrameResultListener) {
        this.onFrameResultListener = onFrameResultListener

        this.udpServerPort = udpSocketHelper.initUdpSockets(udpServerPort)
        val dlUdpPort = this.udpServerPort

        CoroutineScope(bgDispatcher).launch(bgDispatcher) {
            val resultPair =  httpHelper.initUdpStream(dlUdpPort)
            // TODO
            streamingInitialized = true
        }
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
