package com.redbu11.langlearnapp.ui.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseRepository
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application, private val repository: PhraseRepository) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    val phrases = repository.phrases

    /**
     * clear all phrases from the database
     */
    fun clearAllPhrases() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
//            Toast.makeText(
//                context,
//                "$noOfRowsDeleted rows deleted successfully",
//                Toast.LENGTH_SHORT
//            )
//                .show()
        } else {
//            Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    fun importToRepositoryFromCsv(context: Context, fileUri: Uri) = viewModelScope.launch {
        repository.importFromCsvFile(context, fileUri)
    }

    fun exportFromRepositoryToCsv(csvFile: File, phrases: Collection<Phrase>) {
        repository.exportToCSVFile(csvFile, phrases)
    }
}