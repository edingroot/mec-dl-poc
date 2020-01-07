import android.os.Handler
import android.util.Log
import tw.cchi.mec_dl_poc.config.Constants
import java.io.IOException
import java.net.*

class UdpSocketHelper(private val handler: Handler) {
    companion object {
        private const val TAG = Constants.TAG + "/UdpSocketHelper"
        private const val BUFFER_LENGTH = 10240
    }

    // private var mThreadPool: ExecutorService? = null
    private var socket: DatagramSocket? = null
    private var receivePacket: DatagramPacket? = null
    private val receiveByte = ByteArray(BUFFER_LENGTH)

    private var isThreadRunning = false
    private lateinit var clientThread: Thread

    init {
        // val cpuNumbers = Runtime.getRuntime().availableProcessors()
        // mThreadPool = Executors.newFixedThreadPool(cpuNumbers * 5)
    }

    fun initUdpSockets(port: Int): Boolean {
        try {
            if (socket == null)
                socket = DatagramSocket(port)

            if (receivePacket == null)
                receivePacket = DatagramPacket(receiveByte, BUFFER_LENGTH)

            startSocketThread()
        } catch (e: SocketException) {
            e.printStackTrace()
            return false
        }

        return true
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

        if (socket != null) {
            socket?.close()
            socket = null
        }
    }

    fun sendPacket(remoteHost: String, remotePort: Int, message: String): Boolean {
        return sendPacket(remoteHost, remotePort, message.toByteArray(), message.length)
    }

    fun sendPacket(remoteHost: String, remotePort: Int, bytes: ByteArray, length: Int): Boolean {
        return sendPacket(remoteHost, remotePort, bytes, 0, length)
    }

    fun sendPacket(
        remoteHost: String,
        remotePort: Int,
        bytes: ByteArray,
        offset: Int,
        length: Int
    ): Boolean {
        if (socket == null) return false

        // mThreadPool?.execute {
        try {
            val packet = DatagramPacket(
                bytes,
                offset,
                length,
                InetAddress.getByName(remoteHost),
                remotePort
            )
            socket?.send(packet)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // }

        return true
    }

    private fun receiveMessage() {
        while (isThreadRunning) {
            try {
                socket?.receive(receivePacket)

                if (receivePacket == null || receivePacket?.length == 0)
                    continue

                // Multi thread to handle multi packets
                // mThreadPool?.execute {
                val strReceive =
                    String(receivePacket!!.data, receivePacket!!.offset, receivePacket!!.length)

                /* Log.d(
                    TAG,
                    strReceive + " from " + receivePacket!!.address.hostAddress + ":" + receivePacket!!.port
                ) */

                handler.sendMessage(handler.obtainMessage(1, strReceive))
                receivePacket?.length = BUFFER_LENGTH
                // }
            } catch (e: IOException) {
                terminateUdpSockets()
                e.printStackTrace()
                return
            }
        }
    }
}
