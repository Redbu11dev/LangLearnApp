package com.redbu11.langlearnapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class EraseDatabaseDialog : ConfirmationDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete all phrases permanently?")
            .setPositiveButton("Delete") { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton("Cancel") { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            .create()
    }

}