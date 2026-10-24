package com.medtrixa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.medtrixa.data.local.dao.DrugDao
import com.medtrixa.data.local.entity.GenericDrug
import com.medtrixa.data.local.entity.BrandName

@Database(entities = [GenericDrug::class, BrandName::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drugDao(): DrugDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drugs.db"
                )
                .createFromAsset("database/drugs.db")
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
