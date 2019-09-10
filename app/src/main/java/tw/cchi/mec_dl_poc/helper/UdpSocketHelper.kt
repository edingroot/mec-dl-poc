import android.os.Handler
import android.util.Log
import tw.cchi.mec_dl_poc.config.Constants
import java.io.IOException
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UdpSocketHelper(private val handler: Handler) {
    private val TAG = Constants.TAG + "/UdpSocketHelper"
    private val BUFFER_LENGTH = 10240

    private var mThreadPool: ExecutorService? = null

    private var sendSocket: DatagramSocket? = null
    private var recvSocket: DatagramSocket? = null
    private var receivePacket: DatagramPacket? = null
    private val receiveByte = ByteArray(BUFFER_LENGTH)

    private var isThreadRunning = false
    private lateinit var clientThread: Thread

    init {
        val cpuNumbers = Runtime.getRuntime().availableProcessors()
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * 5)
    }

    fun initUdpSockets(portRecv: Int?): Int {
        try {
            if (sendSocket == null)
                sendSocket = DatagramSocket()

            if (recvSocket == null)
                recvSocket = if (portRecv == null) DatagramSocket() else DatagramSocket(portRecv)

            if (receivePacket == null)
                receivePacket = DatagramPacket(receiveByte, BUFFER_LENGTH)

            startSocketThread()
        } catch (e: SocketException) {
            e.printStackTrace()
            return -1
        }

        return sendSocket!!.port
    }

    private fun startSocketThread() {
        if (isThreadRunning) return

        clientThread = Thread(Runnable {
            Log.i(TAG, "UDP client thread is running...")
            receiveMessage()
        })
        isThreadRunning = true
        clientThread.start()
    }

    fun terminateUdpSockets() {
        isThreadRunning = false
        receivePacket = null
        clientThread.interrupt()

        if (sendSocket != null) {
            sendSocket?.close()
            sendSocket = null
        }

        if (recvSocket != null) {
            recvSocket?.close()
            recvSocket = null
        }
    }

    fun sendMessage(remoteHost: String, remotePort: Int, message: String): Boolean {
        if (sendSocket == null) return false

        mThreadPool?.execute {
            try {
                val packet = DatagramPacket(
                    message.toByteArray(),
                    message.length,
                    InetAddress.getByName(remoteHost),
                    remotePort
                )
                sendSocket?.send(packet)
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return true
    }

    private fun receiveMessage() {
        while (isThreadRunning) {
            try {
                recvSocket?.receive(receivePacket)

                if (receivePacket == null || receivePacket?.length == 0)
                    continue

                // Multi thread to handle multi packets
                mThreadPool?.execute {
                    val strReceive =
                        String(receivePacket!!.data, receivePacket!!.offset, receivePacket!!.length)
                    Log.d(
                        TAG,
                        strReceive + " from " + receivePacket!!.address.hostAddress + ":" + receivePacket!!.port
                    )

                    handler.sendMessage(handler.obtainMessage(1, strReceive))
                    receivePacket?.length = BUFFER_LENGTH
                }
            } catch (e: IOException) {
                terminateUdpSockets()
                e.printStackTrace()
                return
            }
        }
    }
}