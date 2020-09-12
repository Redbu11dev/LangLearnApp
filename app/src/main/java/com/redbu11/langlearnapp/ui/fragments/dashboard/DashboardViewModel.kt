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

package com.redbu11.langlearnapp.ui.fragments.dashboard


import android.app.Application
import android.text.TextUtils
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.utils.Event
import kotlinx.coroutines.launch
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.utils.SoftUtils


class DashboardViewModel(application: Application, private val repository: PhraseRepository) :
    AndroidViewModel(application), Observable {

    val phrases = repository.phrases
    private var isUpdateOrDelete = false
    private lateinit var phraseToUpdateOrDelete: Phrase

    val isPhraseCreatorContainerVisible = MutableLiveData<Boolean>()

    @Bindable
    val inputPhraseLang = MutableLiveData<String>()
    @Bindable
    val inputPhraseText = MutableLiveData<String>()
    @Bindable
    val inputTranslationLang = MutableLiveData<String>()
    @Bindable
    val inputTranslationText = MutableLiveData<String>()
    @Bindable
    val inputNotes = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<Int>()
    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<Int>()
    @Bindable
    val deleteButtonVisible = MutableLiveData<Boolean>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        hidePhraseCreatorContainer()
        resetInputForm()
    }

    fun resetInputForm() {
        nullifyInputValues()
        saveOrUpdateButtonText.value = R.string.dashboard_btn_save
        clearAllOrDeleteButtonText.value = R.string.dashboard_btn_clear_all
        deleteButtonVisible.value = false
    }

    fun resetAndShowInputForm() {
        resetInputForm()
        showPhraseCreatorContainer()
    }

    fun saveOrUpdate() {
        if (TextUtils.isEmpty(inputPhraseLang.value)) {
            statusMessage.value = Event(SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_error_enter_phrase_language
            ))
        } else if (TextUtils.isEmpty(inputPhraseText.value)) {
            statusMessage.value = Event(SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_error_enter_phrase_text
            ))
        } else if (TextUtils.isEmpty(inputTranslationLang.value)) {
            statusMessage.value = Event(SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_error_enter_translation_language
            ))
        } else if (TextUtils.isEmpty(inputTranslationText.value)) {
            statusMessage.value = Event(SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_error_enter_translation_text
            ))
        } else {

            if (TextUtils.isEmpty(inputNotes.value)) {
                //statusMessage.value = Event("Please enter notes")
                inputNotes.value = ""
            }

            if (isUpdateOrDelete) {
                phraseToUpdateOrDelete.phraseLanguage = inputPhraseLang.value!!
                phraseToUpdateOrDelete.phraseText = inputPhraseText.value!!
                phraseToUpdateOrDelete.translationLanguage = inputTranslationLang.value!!
                phraseToUpdateOrDelete.translationText = inputTranslationText.value!!
                phraseToUpdateOrDelete.notes = inputNotes.value!!
                update(phraseToUpdateOrDelete)
            } else {
                val phraseLang = inputPhraseLang.value!!
                val phraseText = inputPhraseText.value!!
                val translationLang = inputTranslationLang.value!!
                val translationText = inputTranslationText.value!!
                val notes = inputNotes.value!!
                insert(
                    phrase = Phrase(
                        0,
                        phraseLang,
                        phraseText,
                        translationLang,
                        translationText,
                        notes
                    )
                )

                nullifyInputValues()
            }
        }

    }

    fun delete() {
        delete(phraseToUpdateOrDelete)
    }

    fun insert(phrase: Phrase) = viewModelScope.launch {
        val newRowID = repository.insert(phrase)
        if (newRowID > -1) {
            statusMessage.value = Event("${SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_phrase_insert_success
            )} $newRowID")
            hidePhraseCreatorContainer()
        } else {
            statusMessage.value = Event(
                SoftUtils.getStringFromIdAvm(
                    this@DashboardViewModel,
                    R.string.error_occurred
                )
            )
        }
    }

    fun update(phrase: Phrase) = viewModelScope.launch {
        val noOfRows = repository.update(phrase)
        if (noOfRows > 0) {
            nullifyInputValues()
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = R.string.dashboard_btn_save
            clearAllOrDeleteButtonText.value = R.string.dashboard_btn_clear_all
            deleteButtonVisible.value = false
            statusMessage.value = Event("$noOfRows ${SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_phrase_update_success
            )}")
            hidePhraseCreatorContainer()
        } else {
            statusMessage.value = Event(
                SoftUtils.getStringFromIdAvm(
                    this@DashboardViewModel,
                    R.string.error_occurred
                )
            )
        }
    }

    fun delete(phrase: Phrase) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(phrase)
        if (noOfRowsDeleted > 0) {
            nullifyInputValues()
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = R.string.dashboard_btn_save
            clearAllOrDeleteButtonText.value = R.string.dashboard_btn_clear_all
            deleteButtonVisible.value = false
            statusMessage.value = Event("$noOfRowsDeleted ${SoftUtils.getStringFromIdAvm(
                this@DashboardViewModel,
                R.string.dashboard_phrase_delete_success
            )}")
            hidePhraseCreatorContainer()
        } else {
            statusMessage.value = Event(
                SoftUtils.getStringFromIdAvm(
                    this@DashboardViewModel,
                    R.string.error_occurred
                )
            )
        }
    }

    fun initUpdateAndDelete(phrase: Phrase) {
        inputPhraseLang.value = phrase.phraseLanguage
        inputPhraseText.value = phrase.phraseText
        inputTranslationLang.value = phrase.translationLanguage
        inputTranslationText.value = phrase.translationText
        inputNotes.value = phrase.notes
        isUpdateOrDelete = true
        phraseToUpdateOrDelete = phrase
        saveOrUpdateButtonText.value = R.string.dashboard_btn_update
        clearAllOrDeleteButtonText.value = R.string.dashboard_btn_delete
        deleteButtonVisible.value = true
        showPhraseCreatorContainer()

    }

    fun nullifyInputValues() {
        inputPhraseLang.value = null
        inputPhraseText.value = null
        inputTranslationLang.value = null
        inputTranslationText.value = null
        inputNotes.value = null
    }

    fun showPhraseCreatorContainer() {
        isPhraseCreatorContainerVisible.value = true
    }

    fun hidePhraseCreatorContainer() {
        isPhraseCreatorContainerVisible.value = false
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}
