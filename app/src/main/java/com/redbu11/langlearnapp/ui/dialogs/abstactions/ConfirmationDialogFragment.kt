/*
 *
 *  * Copyright (C) 2020 Viktor Bukovets
 *  *
 *  * This file is part of LangLearnApp.
 *  *
 *  * LangLearnApp is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * LangLearnApp is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package com.redbu11.langlearnapp.ui.dialogs.abstactions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


abstract class ConfirmationDialogFragment : DialogFragment() {

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
            .setMessage("Your message")
            .setPositiveButton("Btn_positive") { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .setNegativeButton("Btn_negative") { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            .create()
    }

    //Usages reminder
//        dialogFragment.setTargetFragment(this, 0)
//        dialogFragment.show(requireActivity().supportFragmentManager, "confirmEraseDatabase")

    //Dismiss reminder
//        public void dismissDialog(){
//            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//            if (prev != null) {
//                DialogFragment df = (DialogFragment) prev;
//                df.dismiss();
//            }
//        }

}