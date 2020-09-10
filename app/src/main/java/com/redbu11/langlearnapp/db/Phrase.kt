package com.redbu11.langlearnapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrase_data_table")
data class Phrase (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "phrase_id")
    var id : Long,

    @ColumnInfo(name = "phrase_language")
    var phraseLanguage : String,

    @ColumnInfo(name = "phrase_text")
    var phraseText : String,

    @ColumnInfo(name = "translation_language")
    var translationLanguage : String,

    @ColumnInfo(name = "translation_text")
    var translationText : String,

    @ColumnInfo(name = "phrase_notes")
    var notes : String
)