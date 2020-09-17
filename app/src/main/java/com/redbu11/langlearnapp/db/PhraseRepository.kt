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

package com.redbu11.langlearnapp.db

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.io.OutputStream

/**
 * Phrase repository
 * @param dao - Phrase data access object
 */
class PhraseRepository(private val dao : PhraseDAO) {

    val phrases = dao.getAllPhrases()

    fun phrasesThatContain(queryString: String) : LiveData<List<Phrase>>{
        return dao.getAllPhrasesThatContain(queryString)
    }

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

    /**
     * Export phrases to CSV
     */
    fun exportToCSVFile(csvFile: OutputStream, phrases: Collection<Phrase>) {
    //fun exportToCSVFile(csvFile: File, phrases: Collection<Phrase>) {
        //csvWriter().open(csvFile, append = false) {
        csvWriter().open(csvFile) {
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

    /**
     * Import phrases from CSV
     */
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

