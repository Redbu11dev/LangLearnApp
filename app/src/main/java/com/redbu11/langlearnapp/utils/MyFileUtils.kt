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

import android.content.Context
import java.io.File

object MyFileUtils {

    /**
     * Generate a file on the internal storage
     */
    fun generateInternalFile(context: Context, fileName: String): File? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    /**
     * Generate a file on the external storage
     */
    fun generateExternalFile(context: Context, fileName: String): File {
        val file = File("${context.getExternalFilesDir(null)?.absolutePath}/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}