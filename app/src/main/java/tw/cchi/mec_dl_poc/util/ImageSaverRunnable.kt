package tw.cchi.mec_dl_poc.util

import android.media.Image
import tw.cchi.mec_dl_poc.helper.FrameProcessor
import java.io.File

/**
 * Saves a JPEG [Image] into the specified [File].
 */
internal class ImageSaverRunnable(
    /**
     * The JPEG image
     */
    private val image: Image,

    /**
     * The file we save the image into.
     */
    private val file: File
) : Runnable {

    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        FrameProcessor.saveJpegByteArray(bytes, file)
    }
}
