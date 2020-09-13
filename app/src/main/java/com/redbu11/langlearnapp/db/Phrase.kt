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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Phrase table
 */
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