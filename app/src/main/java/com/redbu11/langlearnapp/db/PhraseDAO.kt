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

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhraseDAO {
    @Insert
    suspend fun insertPhrase(phrase: Phrase) : Long

    @Update
    suspend fun updatePhrase(phrase: Phrase) : Int

    @Delete
    suspend fun deletePhrase(phrase: Phrase) : Int

    @Query("DELETE FROM phrase_data_table")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM phrase_data_table")
    fun getAllPhrases():LiveData<List<Phrase>>

    @Query("SELECT * FROM phrase_data_table WHERE (phrase_text LIKE :phrase_text)")
    fun getAllPhrasesThatContain(phrase_text: String):LiveData<List<Phrase>>
}