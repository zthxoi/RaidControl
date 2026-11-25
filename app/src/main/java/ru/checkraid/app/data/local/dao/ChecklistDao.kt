package ru.checkraid.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.checkraid.app.data.local.entity.Checklist

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklists ORDER BY id DESC")
    fun getAllChecklists(): Flow<List<Checklist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checklist: Checklist): Long

    @Update
    suspend fun update(checklist: Checklist)

    @Delete
    suspend fun delete(checklist: Checklist)
}