package com.redbu11.langlearnapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


open class ConfirmationDialogFragment : DialogFragment() {

    interface ConfirmationDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?)
        fun onDialogNegativeClick(dialog: DialogFragment?)
    }

    lateinit var listener: ConfirmationDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

//        try {
//            // Instantiate the ConfirmationListener so we can send events to the host
//            listener = activity as ConfirmationDialogListener
//        } catch (e: ClassCastException) {
//            // The activity doesn't implement the interface, throw exception
//            throw ClassCastException(activity.toString() + " must implement ConfirmationListener")
//        }

        try {
            listener = parentFragment as ConfirmationDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement Callback interface")
        }

//        try {
//            listener = targetFragment as ConfirmationDialogListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException("Calling fragment must implement Callback interface")
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        var message = requireArguments().getString("message","")
//        var positiveButtonText = requireArguments().getString("positiveButtonText","")
//        var negativeButtonText = requireArguments().getString("negativeButtonText","")

        return AlertDialog.Builder(requireContext())
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton("No") { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            .create()
    }

}