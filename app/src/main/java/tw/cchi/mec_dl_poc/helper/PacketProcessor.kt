package tw.cchi.mec_dl_poc.helper

import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.util.and
import tw.cchi.mec_dl_poc.util.shl
import kotlin.math.ceil

class PacketProcessor {

    companion object {
        /**
         * @return Pair<packCount, lastPACK>
         */
        fun fragmentPacketByOffset(bytes: ByteArray): Pair<Int, ByteArray> {
            val packCount = ceil(bytes.size.toDouble() / Constants.CHUNK_PACK_SIZE).toInt()
            val lastPACK = ByteArray(Constants.CHUNK_PACK_SIZE)

            // Last PACK
            val offset = (packCount - 1) * Constants.CHUNK_PACK_SIZE
            for (i in 0 until bytes.size - offset) {
                lastPACK[i] = bytes[offset + i]
            }

            return Pair(packCount, lastPACK)
        }

        fun fragmentPacket(bytes: ByteArray): Array<ByteArray> {
            val packCount = ceil(bytes.size.toDouble() / Constants.CHUNK_PACK_SIZE).toInt()
            val chunk = Array(packCount) { ByteArray(Constants.CHUNK_PACK_SIZE) }

            for (i in 0 until packCount - 1) {
                chunk[i] = bytes.copyOfRange(
                    i * Constants.CHUNK_PACK_SIZE,
                    ((i + 1) * Constants.CHUNK_PACK_SIZE)
                )
            }

            // Last PACK
            val offset = (packCount - 1) * Constants.CHUNK_PACK_SIZE
            for (i in 0 until bytes.size - offset) {
                chunk[packCount - 1][i] = bytes[offset + i]
            }

            return chunk
        }

        // Not tested
        fun assemblePacket(dataLength: Int, packetArray: Array<ByteArray>): ByteArray? {
            if (packetArray.size * Constants.CHUNK_PACK_SIZE < dataLength)
                return null

            var byteArray = ByteArray(dataLength)
            for (i in 0 until packetArray.size - 2)
                byteArray += packetArray[i]

            val remainingPackets = dataLength - (packetArray.size - 1) * Constants.CHUNK_PACK_SIZE
            for (i in 0 until remainingPackets - 1)
                byteArray += packetArray[packetArray.size - 1][i]

            return byteArray
        }

        fun getDataLengthIn4Bytes(length: Int): ByteArray {
            val bytes = ByteArray(4)
            signedInt2Bytes(length, bytes, 0, 4)
            return bytes
        }

        private fun signedInt2Bytes(number: Int, bytes: ByteArray, startFrom: Int, bytesN: Int) {
            when (bytesN) {
                2 -> {
                    bytes[startFrom] = (number and 0xff).toByte() // lsb
                    bytes[startFrom + 1] = (number and 0xff00 shr 8).toByte() // msb
                }

                4 -> {
                    bytes[startFrom] = (number and 0xff).toByte() // lsb
                    bytes[startFrom + 1] = (number and 0x0000ff00 shr 8).toByte()
                    bytes[startFrom + 2] = (number and 0x00ff0000 shr 16).toByte()
                    bytes[startFrom + 3] = (number and -0x1000000 shr 24).toByte() // msb
                }

                else -> throw RuntimeException("Unsupported number of bytes: $bytesN")
            }
        }

        private fun bytes2SignedInt(bytes: ByteArray, startFrom: Int, bytesN: Int): Int {
            var result: Int

            when (bytesN) {
                2 -> {
                    result = ((bytes[startFrom + 1] and 0xff) shl 8).toInt() or
                            (bytes[startFrom] and 0xff).toInt()

                    // negative number
                    if (bytes[startFrom + 1] and 0x80 == 0x80.toByte())
                        result = -1 * ((result.inv() and 0xffff) + 1) // 2's complement
                }

                4 -> {
                    result =
                        ((bytes[startFrom + 3] and 0xff) shl 24).toInt() or
                                (bytes[startFrom + 2] and 0xff shl 16).toInt() or
                                (bytes[startFrom + 1] and 0xff shl 8).toInt() or
                                (bytes[startFrom] and 0xff).toInt()

                    // negative number
                    if (bytes[startFrom + 1] and 0x8000 == 0x8000.toByte())
                        result = -1 * (result.inv() + 1) // 2's complement
                }

                else -> throw RuntimeException("Unsupported number of bytes: $bytesN")
            }

            return result
        }
    }
}
