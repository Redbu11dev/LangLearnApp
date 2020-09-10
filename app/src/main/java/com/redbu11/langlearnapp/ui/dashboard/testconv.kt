package com.redbu11.langlearnapp.ui.dashboard

class testconv {
    private fun stringToColour(str: String): String {
        var hash = 0
        for (i in 0 until str.length) {
            hash = str[i].toInt() + ((hash shl 5) - hash)
        }
        var colour = "#"
        for (i in 0..2) {
            val value = hash shr i * 8 and 0xFF
            colour += ("00" + Integer.toString(16)).substring(-2)
        }
        return colour
    }
}