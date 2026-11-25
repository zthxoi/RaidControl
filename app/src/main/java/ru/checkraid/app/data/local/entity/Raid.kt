package ru.checkraid.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "raids")
data class Raid(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val address: String,
    val shopName: String,
    val result: String = RESULT_PLANNED,
    val inspectorName: String,
    val notes: String = "",
    val receiptPhotoUri: String? = null
) {
    companion object {
        const val RESULT_PLANNED = "PLANNED"
        const val RESULT_SUCCESS = "SUCCESS"
        const val RESULT_FAILED = "FAILED"
    }
}