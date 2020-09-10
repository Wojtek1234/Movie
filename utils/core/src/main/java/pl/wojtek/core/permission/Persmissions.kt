package pl.wojtek.core.permission

import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment


/**
 *
 */

class AppPermission(val permissionName: String, val requestCode: Int)

fun isLollipopOrBellow(): Boolean = (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP)
fun isOreoOrAbove(): Boolean = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)

fun Fragment.isPermissionGranted(permission: String) =
    (PermissionChecker.checkSelfPermission(this.requireActivity(), permission) == PermissionChecker.PERMISSION_GRANTED)

fun Fragment.isRationaleNeeded(permission: String) = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)

fun Fragment.requestPermission(permission: String, code: Int) = requestPermissions(arrayOf(permission), code)

fun Fragment.handlePermission(permission: AppPermission,
                              onGranted: (AppPermission) -> Unit,
                              onRationaleNeeded: (AppPermission) -> Unit) {
    when {
        isLollipopOrBellow() || isPermissionGranted(permission.permissionName) -> onGranted(permission)
        isRationaleNeeded(permission.permissionName) -> onRationaleNeeded(permission)
        else -> requestPermission(permission.permissionName, permission.requestCode)
    }
}


fun Fragment.handlePermission(permission: AppPermission,
                              onGranted: (AppPermission) -> Unit,
                              onDenied: (AppPermission) -> Unit,
                              onRationaleNeeded: (AppPermission) -> Unit) {
    when {
        isLollipopOrBellow() || isPermissionGranted(permission.permissionName) -> onGranted(permission)
        isRationaleNeeded(permission.permissionName) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}