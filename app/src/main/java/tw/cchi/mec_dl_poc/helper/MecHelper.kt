package tw.cchi.mec_dl_poc.helper

import UdpSocketHelper
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.coroutines.*
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import java.io.IOException
import java.lang.ref.WeakReference

class MecHelper {
    private val TAG = Constants.TAG + "/MecHelper"

    var streamingInitialized = false
        private set
    private var udpServerPort = 0
    private var onMecResultListener: OnMecResultListener? = null

    // Dispatches execution into Android main thread
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
    // A pool of shared threads as coroutine dispatcher
    private val bgDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val httpHelper = HttpHelper()
    private val packetRecvHandler = PacketRecvHandler(WeakReference(this))
    private val udpSocketHelper = UdpSocketHelper(packetRecvHandler)

    fun initUdpStreaming(udpServerPort: Int?, onMecResultListener: OnMecResultListener) {
        this.onMecResultListener = onMecResultListener

        this.udpServerPort = udpSocketHelper.initUdpSockets(udpServerPort)
        val dlUdpPort = this.udpServerPort

        onMecResultListener.onConnStatusChange(MecConnStatus.CONNECTING)

        val handler = CoroutineExceptionHandler { _, e ->
            onMecResultListener.onConnStatusChange(MecConnStatus.FAILED)
            onMecResultListener.onStatusMessage(e.message.toString())
        }

        CoroutineScope(bgDispatcher).launch(handler) {
            val resultPair =  httpHelper.initUdpStream(dlUdpPort)
            Log.i(TAG, "HTTP udp stream init result: $resultPair")

            if (resultPair != null) {
                onMecResultListener.onConnStatusChange(MecConnStatus.CONNECTED)
                streamingInitialized = true
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
        this.streamingInitialized = false
        this.udpServerPort = 0
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
