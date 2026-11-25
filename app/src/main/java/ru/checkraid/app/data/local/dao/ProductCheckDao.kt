package ru.checkraid.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.checkraid.app.data.local.entity.ProductCheck

@Dao
interface ProductCheckDao {
    @Query("SELECT * FROM product_checks WHERE raidId = :raidId ORDER BY id ASC")
    fun getProductsByRaid(raidId: Long): Flow<List<ProductCheck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productCheck: ProductCheck): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductCheck>)

    @Update
    suspend fun update(productCheck: ProductCheck)

    @Delete
    suspend fun delete(productCheck: ProductCheck)
}