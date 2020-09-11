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