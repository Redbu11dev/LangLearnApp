package com.redbu11.langlearnapp.db

import android.content.Context
import android.net.Uri
import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class PhraseRepository(private val dao : PhraseDAO) {

    val phrases = dao.getAllPhrases()

    suspend fun insert(phrase: Phrase):Long{
        return dao.insertPhrase(phrase)
    }

    suspend fun update(phrase: Phrase):Int{
        return dao.updatePhrase(phrase)
    }

    suspend fun delete(phrase: Phrase) : Int{
        return dao.deletePhrase(phrase)
    }

    suspend fun deleteAll() : Int{
        return dao.deleteAll()
    }

    fun exportToCSVFile(csvFile: File, phrases: Collection<Phrase>) {
        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(
                listOf(
                    "id",
                    "phrase_lang",
                    "phrase_text",
                    "translation_lang",
                    "translation_text",
                    "notes"
                )
            )
            phrases.forEach { phrase ->
                writeRow(
                    listOf(
                        phrase.id,
                        phrase.phraseLanguage,
                        phrase.phraseText,
                        phrase.translationLanguage,
                        phrase.translationText,
                        phrase.notes
                    )
                )
            }
        }
    }

    suspend fun importFromCsvFile(context: Context, fileUri: Uri) {
        val inStream = context.contentResolver.openInputStream(fileUri)
        if (null != inStream) {
            val rows = csvReader().readAll(inStream)
            rows.forEachIndexed {
                    index, list ->
                if (index > 0) {
                    insert(Phrase(0, list[1], list[2], list[3], list[4], list[5]))
                }
            }
            Log.i("CSV input", rows.toString())
        }
        else {
            //inStream is null?!?!
        }
    }
}

