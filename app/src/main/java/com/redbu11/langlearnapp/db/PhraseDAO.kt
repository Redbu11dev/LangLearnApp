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
}