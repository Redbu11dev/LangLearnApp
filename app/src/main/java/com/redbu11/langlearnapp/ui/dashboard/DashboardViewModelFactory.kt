package com.redbu11.langlearnapp.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.redbu11.langlearnapp.db.PhraseRepository
import java.lang.IllegalArgumentException

class DashboardViewModelFactory(private val application: Application, private val repository: PhraseRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            return DashboardViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}

