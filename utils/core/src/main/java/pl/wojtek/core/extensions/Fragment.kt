package pl.wojtek.core.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import pl.wojtek.core.R
import pl.wojtek.core.common.ConsumableValue
import pl.wojtek.core.errors.ErrorWrapper


/**
 *
 */


inline fun Fragment.animateColor(colorFrom: Int, colorTo: Int, crossinline func: (Int) -> Unit): ValueAnimator {
    val colorAnimation =
        ValueAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(requireContext(), colorFrom), ContextCompat.getColor(requireContext(), colorTo))
    colorAnimation.duration = 1000
    colorAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    colorAnimation.start()
    return colorAnimation
}

inline fun Fragment.animateInt(from: Int, to: Int, duration: Long = 1000L, crossinline func: (Int) -> Unit): ValueAnimator {
    val colorAnimation =
        ValueAnimator.ofInt(from, to)
    colorAnimation.duration = duration
    colorAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    colorAnimation.start()
    return colorAnimation
}

fun Fragment.startCameraActivity(requestCode: Int = 0, uri: Uri) {
    startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }, requestCode)
}

fun Fragment.startGalleryActivity(requestCode: Int = 0) {
    startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode)
}

fun <T> Fragment.findReturnLiveData(key: String): LiveData<T>? = findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData(key)
fun Fragment.putReturnValueToLiveData(key: String, value: Any) = findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)

fun Fragment.hideKeyboard() {
    val inputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = activity?.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Fragment.showKeyboard() {
    activity?.currentFocus?.let {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInputFromWindow(
            it.windowToken,
            InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Fragment.adjustPan() = requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
fun Fragment.adjustPanResize() =
    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

fun Fragment.adjustResize() =
    requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)



fun Fragment.showSimpleErrorDialog(consumable: ConsumableValue<ErrorWrapper>) {
    consumable.consume { wrapper ->
        if (wrapper.handle == null) {
            AlertDialog.Builder(requireContext())
                .setTitle(wrapper.title)
                .setMessage(wrapper.text)
                .setCancelable(false)
                .setPositiveButton(R.string.ok) { a, _ ->
                    a.dismiss()
                }
                .show()
        } else {
            wrapper.handle.invoke(this@showSimpleErrorDialog)
        }
    }
}

fun Fragment.showQuestionDialog(title: String, question: String, positive: () -> Unit, negative: () -> Unit = {}) {
    AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(question)
        .setPositiveButton(R.string.ok) { d, _ ->
            positive()
            d.dismiss()
        }.setNegativeButton(R.string.cancel) { d, _ ->
            negative()
            d.dismiss()
        }.setCancelable(false)
        .show()
}


fun Fragment.setStatusBarTransparent() {
    requireActivity().window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }
}


fun Fragment.setStatusBarPrimary() {
    requireActivity().window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
    }
}

fun Fragment.getColor(colorInt: Int) = ContextCompat.getColor(requireContext(), colorInt)

fun Fragment.setToolbarTitle(title: String) {
    findNavController()
}