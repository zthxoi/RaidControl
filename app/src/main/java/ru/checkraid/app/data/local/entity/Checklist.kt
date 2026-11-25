package ru.checkraid.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklists")
data class Checklist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val items: List<String>
)