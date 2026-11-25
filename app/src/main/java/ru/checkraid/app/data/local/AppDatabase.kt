package ru.checkraid.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.checkraid.app.data.local.dao.ChecklistDao
import ru.checkraid.app.data.local.dao.ProductCheckDao
import ru.checkraid.app.data.local.dao.RaidDao
import ru.checkraid.app.data.local.entity.Checklist
import ru.checkraid.app.data.local.entity.ProductCheck
import ru.checkraid.app.data.local.entity.Raid

@Database(
    entities = [Raid::class, ProductCheck::class, Checklist::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun raidDao(): RaidDao
    abstract fun productCheckDao(): ProductCheckDao
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "raid_control_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}