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

package com.redbu11.langlearnapp.ui.fragments.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseRepository
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application, private val repository: PhraseRepository) : AndroidViewModel(application) {

    val phrases = repository.phrases

    /**
     * clear all phrases from the database
     */
    fun clearAllPhrases() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            //TODO show success message
        } else {
            //TODO show error message
        }
    }

    fun importToRepositoryFromCsv(context: Context, fileUri: Uri) = viewModelScope.launch {
        repository.importFromCsvFile(context, fileUri)
    }

    fun exportFromRepositoryToCsv(csvFile: File, phrases: Collection<Phrase>) {
        repository.exportToCSVFile(csvFile, phrases)
    }
}