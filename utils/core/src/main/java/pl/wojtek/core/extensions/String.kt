package pl.wojtek.core.extensions

import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.inSpans


/**
 *
 */



fun String?.doubleOrNull(): Double? {
    return try {
        this?.toDouble()
    } catch (ex: NumberFormatException) {
        null
    }
}


inline fun SpannableStringBuilder.clickable(builderAction: SpannableStringBuilder.() -> Unit, crossinline click: () -> Unit) =
    inSpans(object : ClickableSpan() {
        override fun onClick(widget: View) {
            click()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }, builderAction = builderAction)
