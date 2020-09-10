package com.redbu11.langlearnapp.utils

import android.content.Context
import java.io.File

object MyFileUtils {
    fun generateInternalFile(context: Context, fileName: String): File? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
    fun generateExternalFile(context: Context, fileName: String): File {
        val file = File("${context.getExternalFilesDir(null)?.absolutePath}/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}