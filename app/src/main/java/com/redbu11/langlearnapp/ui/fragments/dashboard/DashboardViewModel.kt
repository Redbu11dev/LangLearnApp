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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.utils.Event
import kotlinx.coroutines.launch
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.ui.dialogs.DeletePhraseConfirmationDialog
import com.redbu11.langlearnapp.utils.SoftUtils


class DashboardViewModel(application: Application, private val repository: PhraseRepository) :
    AndroidViewModel(application), Observable {

    @Bindable
    val mutableQueryString = MutableLiveData<String>("")
    val queryString get() = mutableQueryString as LiveData<String> //does not generate additional properties in javacode

    val phrases =  Transformations.switchMap(mutableQueryString) {query ->
        repository.phrasesThatContain("%${query}%")
    }

    private lateinit var phraseToUpdateOrDelete: Phrase

    @Bindable
    val mutablePhraseCreatorContainerVisible = MutableLiveData<Boolean>()
    val phraseCreatorContainerVisible get() = mutablePhraseCreatorContainerVisible as LiveData<Boolean>

    @Bindable
    val mutableInputPhraseLang = MutableLiveData<String>()

    @Bindable
    val mutableInputPhraseText = MutableLiveData<String>()

    @Bindable
    val mutableInputTranslationLang = MutableLiveData<String>()

    @Bindable
    val mutableInputTranslationText = MutableLiveData<String>()

    @Bindable
    val mutableInputNotes = MutableLiveData<String>()

    @Bindable
    val mutableDeleteButtonVisible = MutableLiveData<Boolean>()

    @Bindable
    val mutablePhraseManipulationTitle = MutableLiveData<Int>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    private val _textToShare = MutableLiveData<Event<Phrase>>()
    val textToShare: LiveData<Event<Phrase>>
        get() = _textToShare

    enum class Dialogs {
        IMPORTANT_NOTICE_PHRASE_PERSISTENCE,
        UPDATE_CONFIRMATION,
        DELETE_CONFIRMATION
    }

    private val _dialogToShow = MutableLiveData<Event<Dialogs>>()
    val dialogToShow: LiveData<Event<Dialogs>>
        get() = _dialogToShow

    init {
        hidePhraseCreatorContainer()
        setInputFormValuesAsCreate()
        showImportantNoticePhrasePersistenceDialog()
    }

    /**
     * Clear input form values
     */
    fun clearInputValues() {
        mutableInputPhraseLang.value = ""
        mutableInputPhraseText.value = ""
        mutableInputTranslationLang.value = ""
        mutableInputTranslationText.value = ""
        mutableInputNotes.value = ""
    }

    /**
     * Set input form as "create"
     */
    fun setInputFormValuesAsCreate() {
        clearInputValues()
        mutableDeleteButtonVisible.value = false
        mutablePhraseManipulationTitle.value = R.string.dashboard_phrase_manipulation_title_create
    }

    /**
     * Set input form as "update"
     */
    fun setInputFormValuesAsUpdate(phrase: Phrase) {
        mutableInputPhraseLang.value = phrase.phraseLanguage
        mutableInputPhraseText.value = phrase.phraseText
        mutableInputTranslationLang.value = phrase.translationLanguage
        mutableInputTranslationText.value = phrase.translationText
        mutableInputNotes.value = phrase.notes

        phraseToUpdateOrDelete = phrase
        mutableDeleteButtonVisible.value = true
        mutablePhraseManipulationTitle.value = R.string.dashboard_phrase_manipulation_title_update
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
        mutablePhraseCreatorContainerVisible.value = true
    }

    /**
     * Hide input form
     */
    fun hidePhraseCreatorContainer() {
        mutablePhraseCreatorContainerVisible.value = false
    }

    fun setPhraseCreatorContainerVisibile(visible: Boolean) {
        mutablePhraseCreatorContainerVisible.value = visible
    }

    /**
     * Check input fields for validity
     */
    fun inputFieldsValid(): Boolean {
        if (TextUtils.isEmpty(mutableInputPhraseLang.value)) {
            postStatusMessage(R.string.dashboard_error_enter_phrase_language)
            return false
        }
        else if (TextUtils.isEmpty(mutableInputPhraseText.value)) {
            postStatusMessage(R.string.dashboard_error_enter_phrase_text)
            return false
        }
        else if (TextUtils.isEmpty(mutableInputTranslationLang.value)) {
            postStatusMessage(R.string.dashboard_error_enter_translation_language)
            return false
        }
        else if (TextUtils.isEmpty(mutableInputTranslationText.value)) {
            postStatusMessage(R.string.dashboard_error_enter_translation_text)
            return false
        }
        else {
            return true
        }
    }

    /**
     * Update current phrase
     */
    fun updateCurrentPhrase() {
        if (inputFieldsValid()) {
            phraseToUpdateOrDelete.phraseLanguage = mutableInputPhraseLang.value ?: ""
            phraseToUpdateOrDelete.phraseText = mutableInputPhraseText.value ?: ""
            phraseToUpdateOrDelete.translationLanguage = mutableInputTranslationLang.value ?: ""
            phraseToUpdateOrDelete.translationText = mutableInputTranslationText.value ?: ""
            phraseToUpdateOrDelete.notes = mutableInputNotes.value ?: ""
            update(phraseToUpdateOrDelete)
        }
    }

    /**
     * save current(new) phrase
     */
    fun saveCurrentPhrase() {
        if (inputFieldsValid()) {
            insert(
                Phrase(
                    0,
                    mutableInputPhraseLang.value ?: "",
                    mutableInputPhraseText.value ?: "",
                    mutableInputTranslationLang.value ?: "",
                    mutableInputTranslationText.value ?: "",
                    mutableInputNotes.value ?: ""
                )
            )
            clearInputValues()
        }
    }

    /**
     * get current(open) phrase
     */
    fun getCurrentPhrase(): Phrase {
        return phraseToUpdateOrDelete
    }

    /**
     * Insert phrase
     * @param phrase - Phrase object
     */
    fun insert(phrase: Phrase) = viewModelScope.launch {
        val newRowID = repository.insert(phrase)
        if (newRowID > -1) {
            postStatusMessage(
                "${getStringFromIdResource(R.string.dashboard_phrase_insert_success)}"
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
//            postStatusMessage(
//                "$noOfRowsDeleted ${getStringFromIdResource(R.string.dashboard_phrase_delete_success)}"
//            )
            hidePhraseCreatorContainer()
        } else {
            postStatusMessage(R.string.error_occurred)
        }
    }

    /**
     * delete current phrase
     */
    fun delete() {
        delete(phraseToUpdateOrDelete)
    }

    fun clearQueryString() {
        mutableQueryString.value = ""
    }

    fun setQueryString(string: String) {
        mutableQueryString.value = string
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

    fun getQueryDescStringWith(string: String): String {
        return String.format(getStringFromIdResource(R.string.dashboard_search_query_desc), string)
    }

    fun shareCurrentPhraseAsText() {
        _textToShare.value = Event(getCurrentPhrase())
    }

    fun showImportantNoticePhrasePersistenceDialog() {
        _dialogToShow.value = Event(Dialogs.IMPORTANT_NOTICE_PHRASE_PERSISTENCE)
    }

    fun showConfirmUpdatePhraseDialog() {
        _dialogToShow.value = Event(Dialogs.UPDATE_CONFIRMATION)
    }

    fun showConfirmDeletePhraseDialog() {
        _dialogToShow.value = Event(Dialogs.DELETE_CONFIRMATION)
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
