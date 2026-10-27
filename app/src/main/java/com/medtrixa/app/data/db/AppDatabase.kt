package com.medtrixa.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [IngredientEntity::class, ProductEntity::class, BrandEntity::class, DrugLabelEntity::class],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drugDao(): DrugDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val app = context.applicationContext
                    app.deleteDatabase("drugs.db")
                    app.deleteDatabase("drugs_v7.db")
                    app.deleteDatabase("drugs_v8.db")
                    app.deleteDatabase("drugs_v9.db")
                            app.deleteDatabase("drugs_v10.db")
                            app.deleteDatabase("drugs_v11.db")
                    Room.databaseBuilder(app, AppDatabase::class.java, "drugs_v12.db")
                        .createFromAsset("database/drugs.db")
                        .build()
                }.also { INSTANCE = it }
            }
        }
    }
}
