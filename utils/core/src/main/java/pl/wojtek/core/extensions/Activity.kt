package pl.wojtek.core.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 *
 */



fun Activity.openSettingsPage(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}