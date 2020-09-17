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

package com.redbu11.langlearnapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.ui.dialogs.abstactions.ConfirmationDialogFragment


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