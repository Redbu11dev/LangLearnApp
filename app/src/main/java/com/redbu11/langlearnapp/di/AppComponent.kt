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

import com.redbu11.langlearnapp.LangLearnApp
import com.redbu11.langlearnapp.MainActivity
import com.redbu11.langlearnapp.ui.fragments.dashboard.DashboardFragment
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, BuildersModule::class, AppModule::class, RoomModule::class])
interface AppComponent: AndroidInjector<DaggerApplication> {
    fun inject(application: LangLearnApp)
}