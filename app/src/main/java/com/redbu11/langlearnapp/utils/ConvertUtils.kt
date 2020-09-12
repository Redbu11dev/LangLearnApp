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

object ConvertUtils {

    /**
     * Convert a string to color using hash
     */
    fun stringToColor(str: String): String {
            var hash = 0
            for (i in 0 until str.length) {
                hash = str[i].toInt() + ((hash shl 5) - hash)
            }
            var color = "#"
            for (i in 0..2) {
                val value = hash shr i * 8 and 0xFF
                //colour += ("00${value.toString(16)}").substring(-2)
                var strTemp = ("00${value.toString(16)}")
                color += strTemp.substring(strTemp.length-2)
            }
            return color
        }
}