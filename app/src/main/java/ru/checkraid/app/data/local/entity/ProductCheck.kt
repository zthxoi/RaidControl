package ru.checkraid.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_checks",
    foreignKeys = [
        ForeignKey(
            entity = Raid::class,
            parentColumns = ["id"],
            childColumns = ["raidId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["raidId"])]
)
data class ProductCheck(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val raidId: Long,
    val productName: String,
    val price: Double,
    val expectedPrice: Double,
    val isMatch: Boolean,
    val photoUri: String? = null
)