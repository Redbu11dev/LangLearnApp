package com.redbu11.langlearnapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

object SoftUtils {
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // handle exception
            e.printStackTrace()
        }

    }
}