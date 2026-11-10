package com.wafeer.app.presentation.ui.editor.receipt

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer

object BarcodeReader {

    private val hints = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.CHARACTER_SET to "UTF-8",
        DecodeHintType.ALSO_INVERTED to true,
    )

    fun read(imageProxy: ImageProxy): String? {
        return try {
            val plane = imageProxy.planes.firstOrNull() ?: return null
            val buffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val width = imageProxy.width
            val height = imageProxy.height
            val rotation = imageProxy.imageInfo.rotationDegrees

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

            // Correct orientation of YUV luminance buffer
            val (finalBytes, finalWidth, finalHeight) = rotateYuv(bytes, width, height, rotation)

            val source = PlanarYUVLuminanceSource(
                finalBytes,
                finalWidth,
                finalHeight,
                0, 0,
                finalWidth,
                finalHeight,
                false,
            )

            decodeLuminanceSource(source)
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

            val directResult = decodeLuminanceSource(source)
            if (directResult != null) return directResult

            // If high resolution, downscale to ~1000px as ZXing finder pattern detector is sensitive to giant modules
            val maxSide = maxOf(width, height)
            if (maxSide > 1200) {
                val scale = 1000f / maxSide
                val scaledW = (width * scale).toInt()
                val scaledH = (height * scale).toInt()
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true)
                try {
                    val scaledPixels = IntArray(scaledW * scaledH)
                    scaledBitmap.getPixels(scaledPixels, 0, scaledW, 0, 0, scaledW, scaledH)
                    val scaledSource = RGBLuminanceSource(scaledW, scaledH, scaledPixels)
                    val scaledResult = decodeLuminanceSource(scaledSource)
                    if (scaledResult != null) return scaledResult
                } finally {
                    if (scaledBitmap != bitmap && !scaledBitmap.isRecycled) {
                        scaledBitmap.recycle()
                    }
                }
            }

            null
        } catch (_: Exception) {
            null
        }
    }

    private fun decodeLuminanceSource(source: LuminanceSource): String? {
        val reader = MultiFormatReader().apply { setHints(hints) }

        // 1. Try Hybrid binarizer on full source
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
        } catch (_: Exception) {
            reader.reset()
        }

        // 2. Try GlobalHistogram binarizer on full source
        try {
            return reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source))).text
        } catch (_: Exception) {
            reader.reset()
        }

        // 3. Try center crop (60% of framing area)
        val w = source.width
        val h = source.height
        val cropW = (w * 0.6f).toInt()
        val cropH = (h * 0.6f).toInt()
        if (cropW > 50 && cropH > 50) {
            val cropLeft = (w - cropW) / 2
            val cropTop = (h - cropH) / 2
            val cropped = source.crop(cropLeft, cropTop, cropW, cropH)

            try {
                return reader.decodeWithState(BinaryBitmap(HybridBinarizer(cropped))).text
            } catch (_: Exception) {
                reader.reset()
            }

            try {
                return reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(cropped))).text
            } catch (_: Exception) {
                reader.reset()
            }
        }

        return null
    }

    private fun rotateYuv(bytes: ByteArray, width: Int, height: Int, degrees: Int): Triple<ByteArray, Int, Int> {
        return when (degrees) {
            90 -> {
                val rotated = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        rotated[x * height + (height - y - 1)] = bytes[y * width + x]
                    }
                }
                Triple(rotated, height, width)
            }
            180 -> {
                val rotated = ByteArray(width * height)
                for (i in bytes.indices) {
                    rotated[bytes.size - 1 - i] = bytes[i]
                }
                Triple(rotated, width, height)
            }
            270 -> {
                val rotated = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        rotated[(width - x - 1) * height + y] = bytes[y * width + x]
                    }
                }
                Triple(rotated, height, width)
            }
            else -> Triple(bytes, width, height)
        }
    }
}
