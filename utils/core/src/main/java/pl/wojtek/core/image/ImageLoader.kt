package pl.wojtek.core.image

import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 *
 */


class ImageLoader(private val picasso: Picasso) {
    fun loadImageToImageView(url: String?, imageView: ImageView) {
        picasso.load(url).into(imageView)
    }
}