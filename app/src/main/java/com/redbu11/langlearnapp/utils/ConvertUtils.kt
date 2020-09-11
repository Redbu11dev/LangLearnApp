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