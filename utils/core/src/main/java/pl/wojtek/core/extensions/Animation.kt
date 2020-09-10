package pl.wojtek.core.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils

/**
 *
 */


inline fun Context.animateColor(colorFrom: Int, colorTo: Int, duration: Long = 1000, crossinline func: (Int) -> Unit): ValueAnimator {
    val colorAnimation =
        ValueAnimator.ofObject(
            ArgbEvaluator(), try {
                ContextCompat.getColor(this, colorFrom)
            } catch (ex: Resources.NotFoundException) {
                colorFrom
            }, try {
                ContextCompat.getColor(this, colorTo)
            } catch (ex: Resources.NotFoundException) {
                colorTo
            }
        )
    colorAnimation.duration = duration
    colorAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    colorAnimation.start()
    return colorAnimation
}

inline fun Context.blendColor(colorFrom: Int, colorTo: Int, percent: Float): Int {
    val startColor = ContextCompat.getColor(this, colorFrom)
    val endColor = ContextCompat.getColor(this, colorTo)
    return ColorUtils.blendARGB(startColor, endColor, percent)
}

inline fun animateInt(from: Int, to: Int, duration: Long = 1000L, crossinline func: (Int) -> Unit): ValueAnimator {
    val intAnimation =
        ValueAnimator.ofInt(from, to)
    intAnimation.duration = duration
    intAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    intAnimation.start()
    return intAnimation
}


inline fun animateFloat(vararg values: Float, duration: Long = 1000L, crossinline func: (Float) -> Unit): ValueAnimator {
    val intAnimation =
        ValueAnimator.ofFloat(*values)
    intAnimation.duration = duration
    intAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Float)
    }
    intAnimation.start()
    return intAnimation
}

