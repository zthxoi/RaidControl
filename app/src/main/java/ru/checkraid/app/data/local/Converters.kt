package ru.checkraid.app.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(items: List<String>): String {
        return items.joinToString(separator = SEPARATOR)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        if (data.isBlank()) return emptyList()
        return data.split(SEPARATOR)
    }

    companion object {
        private const val SEPARATOR = "|||"
    }
}