package com.redbu11.langlearnapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.redbu11.langlearnapp.R


class ImportPhrasesDialog : ConfirmationDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(String.format(getString(R.string.dialog_import_phrases_message), getString(R.string.default_export_path)))
            .setPositiveButton(getString(R.string.dialog_import_phrases_btn_positive)) { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton(getString(R.string.dialog_import_phrases_btn_negative)) { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            .create()
    }

}