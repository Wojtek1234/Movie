package pl.wojtek.core.extensions

import android.os.Build
import android.provider.Settings.Global.getString
import android.text.Html
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

/**
 *
 */



fun TextView.fromHtml(id:Int){
   text =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(resources.getString(id), Html.FROM_HTML_MODE_LEGACY)
    }else{
        Html.fromHtml(resources.getString(id))
    }
}

fun TextView.fromHtml(aa:String){
    text =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(aa, Html.FROM_HTML_MODE_LEGACY).toString()
    }else{
        Html.fromHtml(aa).toString()
    }
}

fun EditText.setMultiLineCapSentencesAndDoneAction() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}