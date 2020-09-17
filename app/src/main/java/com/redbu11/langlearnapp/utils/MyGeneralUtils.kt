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

package com.redbu11.langlearnapp.utils

import android.app.Activity
import android.content.Context

object MyGeneralUtils {

    /**
     * set a boolean preference value
     * @param activity - activity
     * @param prefNameResId - string resource id with the pref name
     * @param value - boolean value to set
     */
    fun setBooleanPreferenceValue(activity: Activity, prefNameResId: Int, value: Boolean) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(activity.getString(prefNameResId), value)
            commit()
        }
    }

    /**
     * get a boolean preference value
     * @param activity - activity
     * @param prefNameResId - string resource id with the pref name
     * @param defaultValue - default value
     * @return the value of the preference or default value
     */
    fun getBooleanPreferenceValue(activity: Activity, prefNameResId: Int, defaultValue: Boolean): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return defaultValue
        return sharedPref.getBoolean(activity.getString(prefNameResId), defaultValue)
    }
}