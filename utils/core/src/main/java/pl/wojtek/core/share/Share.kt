package pl.wojtek.core.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import pl.wojtek.core.R

/**
 *
 */


fun Context.share(title: String, uri: Uri, text: String): Intent {
    val intent = getIntent(title, uri, text)
    return Intent.createChooser(intent, getString(R.string.choose_app_to_share))
}


private fun getIntent(title: String, uri: Uri, text: String): Intent {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_SUBJECT, title)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    return intent
}