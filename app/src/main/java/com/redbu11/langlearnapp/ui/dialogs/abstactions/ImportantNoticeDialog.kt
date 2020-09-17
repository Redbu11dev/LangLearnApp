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
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.redbu11.langlearnapp.R


abstract class ImportantNoticeDialog : ConfirmationDialogFragment() {

    interface ImportantNoticeDialogListener {
        fun onImportantNoticeDialogCheckBoxClick(dialog: DialogFragment, boolean: Boolean)
    }

    lateinit var checkBoxView: View
    lateinit var checkBox: CheckBox
    private lateinit var checkBoxListener: ImportantNoticeDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            checkBoxListener = parentFragment as ImportantNoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement Callback interface")
        }

        checkBoxView = View.inflate(context, R.layout.checkbox, null)
        checkBox = checkBoxView.findViewById(R.id.checkbox) as CheckBox
        checkBox.setText(R.string.dialog_phrase_persistence_confirmation_message)
        checkBox.setOnCheckedChangeListener {
                _, b ->
            checkBoxListener.onImportantNoticeDialogCheckBoxClick(this, b)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_info_outline_black_24dp)
            .setTitle("Your title")
            .setMessage("Your message")
            .setView(checkBoxView)
            .setPositiveButton("Btn_positive") { _, _ ->
                listener.onDialogPositiveClick(this)
            }
            .create()
    }

}