package com.redbu11.langlearnapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.redbu11.langlearnapp.R


class EraseDatabaseDialog : ConfirmationDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.dialog_erase_db_message))
            .setPositiveButton(getString(R.string.dialog_erase_db_btn_positive)) { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton(getString(R.string.dialog_erase_db_btn_negative)) { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            .create()
    }

}