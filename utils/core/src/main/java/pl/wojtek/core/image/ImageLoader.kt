package pl.wojtek.core.image

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 *
 */


class ImageLoader(private val picasso: Picasso) {
    fun loadImageToImageView(url: String?, imageView: ImageView) {
        picasso.load(url).into(imageView)
    }

    fun loadImageToImageView(url: String?, imageView: ImageView, callback: (Boolean) -> Unit) {
        picasso.load(url).into(imageView, object : Callback {
            override fun onSuccess() {
                callback(true)
            }

            override fun onError(e: Exception?) {
                callback(false)
            }
        })
    }
}