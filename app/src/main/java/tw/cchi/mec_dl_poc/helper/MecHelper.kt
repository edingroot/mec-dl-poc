package tw.cchi.mec_dl_poc.helper

import UdpSocketHelper
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.coroutines.*
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import java.lang.ref.WeakReference

class MecHelper {
    private val TAG = Constants.TAG + "/MecHelper"

    var streamingInitialized = false
        private set
    private var udpServerPort = 0
    private var remoteUdpPort = 0
    private var remoteUdpTimeout = 0
    private var onMecResultListener: OnMecResultListener? = null

    private val httpHelper = HttpHelper()
    private val packetRecvHandler = PacketRecvHandler(WeakReference(this))
    private val udpSocketHelper = UdpSocketHelper(packetRecvHandler)

    fun initUdpStreaming(udpServerPort: Int, onMecResultListener: OnMecResultListener) {
        this.onMecResultListener = onMecResultListener
        this.udpServerPort = udpServerPort

        onMecResultListener.onConnStatusChange(MecConnStatus.CONNECTING)

        if (!udpSocketHelper.initUdpSockets(udpServerPort)) {
            onMecResultListener.onConnStatusChange(MecConnStatus.FAILED)
            onMecResultListener.onStatusMessage("Failed to open udp sockets")
            return
        }

        val handler = CoroutineExceptionHandler { _, e ->
            onMecResultListener.onConnStatusChange(MecConnStatus.FAILED)
            onMecResultListener.onStatusMessage(e.message.toString())
        }

        CoroutineScope(Dispatchers.IO).launch(handler) {
            val resultPair =  httpHelper.initUdpStream(udpServerPort)
            Log.i(TAG, "HTTP udp stream init result: $resultPair")

            if (resultPair != null) {
                remoteUdpPort = resultPair.first
                remoteUdpTimeout = resultPair.second
                streamingInitialized = true

                onMecResultListener.onConnStatusChange(MecConnStatus.CONNECTED)
                onMecResultListener.onStatusMessage("Remote UDP Port: $remoteUdpPort")
                onMecResultListener.onStatusMessage("Remote UDP Timeout: $remoteUdpTimeout")
            } else {
                onMecResultListener.onConnStatusChange(MecConnStatus.FAILED)
            }
        }

//        // Example
//        runBlocking {
//            val handler = CoroutineExceptionHandler { _, exception ->
//                println("Caught $exception with suppressed ${exception.suppressed.contentToString()}")
//            }
//            val job = GlobalScope.launch(handler) {
//                throw IOException()
//            }
//            job.join()
//        }
    }

    fun terminateUdpStreaming() {
        // TODO: send disconnect message to server

        onMecResultListener?.onConnStatusChange(MecConnStatus.DISCONNECTED)
        streamingInitialized = false
        udpServerPort = 0
        remoteUdpPort = 0
        remoteUdpTimeout = 0
    }

    fun sendUdpString(message: String) {
        udpSocketHelper.sendMessage(Constants.MEC_SERVER_IP, remoteUdpPort, message)
    }

    class PacketRecvHandler(private val outerClass: WeakReference<MecHelper>) : Handler() {
        override fun handleMessage(msg: Message?) {
            outerClass.get()?.onMecResultListener?.onFrameResult(msg?.obj.toString())
        }
    }

    interface OnMecResultListener {
        fun onConnStatusChange(status: MecConnStatus)
        fun onStatusMessage(message: String)
        fun onFrameResult(response: String)
    }
}
