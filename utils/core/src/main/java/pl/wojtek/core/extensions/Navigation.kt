package pl.wojtek.core.extensions

import android.os.Bundle
import androidx.navigation.NavController

/**
 *
 */



fun NavController.navigateWithCheck(checkState:Int, action:Int, bundle: Bundle?= null){
    if (currentDestination?.id == checkState)
        navigate(action, bundle)
}
