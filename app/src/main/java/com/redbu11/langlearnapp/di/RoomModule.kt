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

package com.redbu11.langlearnapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.redbu11.langlearnapp.db.PhraseDAO
import com.redbu11.langlearnapp.db.PhraseDAO_Impl
import com.redbu11.langlearnapp.db.PhraseDatabase
import com.redbu11.langlearnapp.db.PhraseRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    fun providePhraseDatabase(app: Application): PhraseDatabase {
        //need application context
        return Room.databaseBuilder(app, PhraseDatabase::class.java, "phrase_data_database").build()
    }

    @Provides
    @Singleton
    fun providePhraseDao(phraseDatabase: PhraseDatabase) = phraseDatabase.phraseDAO

    @Provides
    @Singleton
    fun providePhraseRepository(dao: PhraseDAO) : PhraseRepository {
        return PhraseRepository(dao)
    }
}