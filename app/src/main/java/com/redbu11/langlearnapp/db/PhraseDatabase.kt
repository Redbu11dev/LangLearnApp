package com.redbu11.langlearnapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Phrase::class],version = 1)
abstract class PhraseDatabase : RoomDatabase() {

    abstract val phraseDAO : PhraseDAO

    //singleton
    companion object{
        @Volatile
        private var INSTANCE : PhraseDatabase? = null
        fun getInstance(context: Context):PhraseDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PhraseDatabase::class.java,
                        "phrase_data_database"
                    ).build()
                }
                return instance
            }
        }

    }
}
