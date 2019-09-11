package tw.cchi.mec_dl_poc.helper

import android.graphics.Bitmap
import android.util.Log
import android.view.TextureView
import tw.cchi.mec_dl_poc.config.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FrameProcessor {
    companion object {
        private const val TAG = Constants.TAG + "/FrameProcessor"

        fun getJpegByteArray(textureView: TextureView, scaleRatio: Double, quality: Int): ByteArray {
            val frame =
                Bitmap.createBitmap(textureView.width, textureView.height, Bitmap.Config.ARGB_8888)

            val bitmap = textureView.getBitmap(frame)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scaleRatio).toInt(),
                (bitmap.height * scaleRatio).toInt(),
                false
            )

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            return stream.toByteArray()
        }

        fun saveJpegByteArray(byteArray: ByteArray, file: File) {
            var output: FileOutputStream? = null
            try {
                output = FileOutputStream(file).apply {
                    write(byteArray)
                }
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            } finally {
                output?.let {
                    try {
                        it.close()
                    } catch (e: IOException) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        }
    }

}
