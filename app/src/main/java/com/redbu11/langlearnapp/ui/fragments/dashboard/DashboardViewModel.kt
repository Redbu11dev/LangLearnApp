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

    @Bindable
    val phraseManipulationTitle = MutableLiveData<Int>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        hidePhraseCreatorContainer()
        setInputFormValuesAsCreate()
    }

    /**
     * Clear input form values
     */
    fun clearInputValues() {
        inputPhraseLang.value = ""
        inputPhraseText.value = ""
        inputTranslationLang.value = ""
        inputTranslationText.value = ""
        inputNotes.value = ""
    }

    /**
     * Set input form as "create"
     */
    fun setInputFormValuesAsCreate() {
        clearInputValues()
        isUpdateOrDelete = false
        saveOrUpdateButtonText.value = R.string.dashboard_btn_save
        clearAllOrDeleteButtonText.value = R.string.dashboard_btn_clear_all
        deleteButtonVisible.value = false
        phraseManipulationTitle.value = R.string.dashboard_phrase_manipulation_title_create
    }

    /**
     * Set input form as "update"
     */
    fun setInputFormValuesAsUpdate(phrase: Phrase) {
        inputPhraseLang.value = phrase.phraseLanguage
        inputPhraseText.value = phrase.phraseText
        inputTranslationLang.value = phrase.translationLanguage
        inputTranslationText.value = phrase.translationText
        inputNotes.value = phrase.notes

        phraseToUpdateOrDelete = phrase
        isUpdateOrDelete = true
        saveOrUpdateButtonText.value = R.string.dashboard_btn_update
        clearAllOrDeleteButtonText.value = R.string.dashboard_btn_delete
        deleteButtonVisible.value = true
        phraseManipulationTitle.value = R.string.dashboard_phrase_manipulation_title_update
    }

    /**
     * Set input form as "create" and show
     */
    fun setAndShowInputFormAsCreate() {
        setInputFormValuesAsCreate()
        showPhraseCreatorContainer()
    }

    /**
     * Set input form as "update" and show
     * @param phrase - Phrase object
     */
    fun setAndShowInputFormAsUpdate(phrase: Phrase) {
        setInputFormValuesAsUpdate(phrase)
        showPhraseCreatorContainer()
    }

    /**
     * Show input form
     */
    fun showPhraseCreatorContainer() {
        isPhraseCreatorContainerVisible.value = true
    }

    /**
     * Hide input form
     */
    fun hidePhraseCreatorContainer() {
        isPhraseCreatorContainerVisible.value = false
    }

    /**
     * Save or update current phrase
     */
    fun saveOrUpdate() {
        if (TextUtils.isEmpty(inputPhraseLang.value)) {
            postStatusMessage(R.string.dashboard_error_enter_phrase_language)
        } else if (TextUtils.isEmpty(inputPhraseText.value)) {
            postStatusMessage(R.string.dashboard_error_enter_phrase_text)
        } else if (TextUtils.isEmpty(inputTranslationLang.value)) {
            postStatusMessage(R.string.dashboard_error_enter_translation_language)
        } else if (TextUtils.isEmpty(inputTranslationText.value)) {
            postStatusMessage(R.string.dashboard_error_enter_translation_text)
        } else {
            if (isUpdateOrDelete) {
                phraseToUpdateOrDelete.phraseLanguage = inputPhraseLang.value ?: ""
                phraseToUpdateOrDelete.phraseText = inputPhraseText.value ?: ""
                phraseToUpdateOrDelete.translationLanguage = inputTranslationLang.value ?: ""
                phraseToUpdateOrDelete.translationText = inputTranslationText.value ?: ""
                phraseToUpdateOrDelete.notes = inputNotes.value ?: ""
                update(phraseToUpdateOrDelete)
            } else {
                insert(
                    Phrase(
                        0,
                        inputPhraseLang.value ?: "",
                        inputPhraseText.value ?: "",
                        inputTranslationLang.value ?: "",
                        inputTranslationText.value ?: "",
                        inputNotes.value ?: ""
                    )
                )
                clearInputValues()
            }
        }

    }

    /**
     * delete current phrase
     */
    fun delete() {
        delete(phraseToUpdateOrDelete)
    }

    /**
     * Insert phrase
     * @param phrase - Phrase object
     */
    fun insert(phrase: Phrase) = viewModelScope.launch {
        val newRowID = repository.insert(phrase)
        if (newRowID > -1) {
            postStatusMessage(
                "${getStringFromIdResource(R.string.dashboard_phrase_insert_success)} $newRowID"
            )
            hidePhraseCreatorContainer()
        } else {
            postStatusMessage(R.string.error_occurred)
        }
    }

    /**
     * Update phrase
     * @param phrase - Phrase object
     */
    fun update(phrase: Phrase) = viewModelScope.launch {
        val noOfRows = repository.update(phrase)
        if (noOfRows > 0) {
            setInputFormValuesAsCreate()
            postStatusMessage(
                "$noOfRows ${getStringFromIdResource(R.string.dashboard_phrase_update_success)}"
            )
            hidePhraseCreatorContainer()
        } else {
            postStatusMessage(R.string.error_occurred)
        }
    }

    /**
     * Delete phrase
     * @param phrase - Phrase object
     */
    fun delete(phrase: Phrase) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(phrase)
        if (noOfRowsDeleted > 0) {
            setInputFormValuesAsCreate()
            postStatusMessage(
                "$noOfRowsDeleted ${getStringFromIdResource(R.string.dashboard_phrase_delete_success)}"
            )
            hidePhraseCreatorContainer()
        } else {
            postStatusMessage(R.string.error_occurred)
        }
    }

    /**
     * Post status message using string res id
     */
    fun postStatusMessage(stringResId: Int) {
        postStatusMessage(
            SoftUtils.getStringFromIdAvm(this, stringResId)
        )
    }

    /**
     * Post status message using string res id
     */
    fun postStatusMessage(string: String) {
        statusMessage.value = Event(string)
    }

    /**
     * Shorter form for getting string from id within the ViewModel
     */
    fun getStringFromIdResource(id: Int): String {
        return SoftUtils.getStringFromIdAvm(this, id)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}
