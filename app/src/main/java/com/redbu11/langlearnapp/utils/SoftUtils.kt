package com.redbu11.langlearnapp.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel

object SoftUtils {

    /**
     * Hide keyboard
     */
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // handle exception
            e.printStackTrace()
        }

    }

    /**
     * Convenient way to get string from id inside an AndroidViewModel
     */
    fun getStringFromIdAvm(androidViewModel: AndroidViewModel, id: Int): String {
        return androidViewModel.getApplication<Application>().getString(id)
    }
}