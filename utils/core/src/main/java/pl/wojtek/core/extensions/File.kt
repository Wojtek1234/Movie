package pl.wojtek.core.extensions

import android.graphics.Bitmap
import java.io.File

/**
 *
 */


fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}
