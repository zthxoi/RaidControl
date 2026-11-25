package ru.checkraid.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.checkraid.app.data.local.entity.Raid

@Dao
interface RaidDao {
    @Query("SELECT * FROM raids ORDER BY id DESC")
    fun getAllRaids(): Flow<List<Raid>>

    @Query("SELECT * FROM raids WHERE id = :id")
    suspend fun getRaidById(id: Long): Raid?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(raid: Raid): Long

    @Update
    suspend fun update(raid: Raid)

    @Delete
    suspend fun delete(raid: Raid)
}