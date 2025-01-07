package com.example.appgestiondatos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appgestiondatos.model.Car

@Database(entities = [Car::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun carDao(): CarDao

  companion object {
    var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      if (INSTANCE == null) {
        INSTANCE = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "dbCar"
        ).build()
      }
      return INSTANCE!!
    }
  }
}