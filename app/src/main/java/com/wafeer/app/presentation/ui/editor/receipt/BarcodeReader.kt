package com.wafeer.app.presentation.ui.editor.receipt

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

object BarcodeReader {

    private val hints = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to listOf(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
        ),
        DecodeHintType.TRY_HARDER to true,
    )

    fun read(imageProxy: ImageProxy): String? {
        return try {
            val plane = imageProxy.planes.firstOrNull() ?: return null
            val buffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val width = imageProxy.width
            val height = imageProxy.height

            // Copy Y-plane (luminance)
            val bytes = ByteArray(width * height)
            val byteBuffer = buffer.duplicate()
            byteBuffer.rewind()

            if (rowStride == width && pixelStride == 1) {
                byteBuffer.get(bytes, 0, minOf(byteBuffer.remaining(), bytes.size))
            } else {
                for (row in 0 until height) {
                    val rowStart = row * rowStride
                    byteBuffer.position(rowStart)
                    for (col in 0 until width) {
                        bytes[row * width + col] = byteBuffer.get(rowStart + col * pixelStride)
                    }
                }
            }

            val source = PlanarYUVLuminanceSource(
                bytes,
                width,
                height,
                0, 0,
                width,
                height,
                false,
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader().apply { setHints(hints) }
            reader.decodeWithState(binaryBitmap).text
        } catch (_: Exception) {
            null
        }
    }

    fun read(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader().apply { setHints(hints) }
            reader.decodeWithState(binaryBitmap).text
        } catch (_: Exception) {
            null
        }
    }
}
