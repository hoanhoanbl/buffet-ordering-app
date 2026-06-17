package com.example.appgoimon.ui.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

/**
 * Renders [content] (e.g. a VietQR EMVCo payload) into a QR-code [ImageBitmap], fully OFFLINE via
 * ZXing. Returns null for blank content or on any encoding failure so callers can show a fallback.
 */
fun generateQrBitmap(content: String?, sizePx: Int = 600): ImageBitmap? {
    if (content.isNullOrBlank()) {
        return null
    }

    return try {
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val width = matrix.width
        val height = matrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
