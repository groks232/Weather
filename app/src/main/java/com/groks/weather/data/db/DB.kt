package com.groks.weather.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity
data class CityDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val city: String
)

@Dao
interface DAO {
    @Query("SELECT * FROM CityDB")
    suspend fun getAllHistoryItems(): List<CityDB>

    @Insert(entity = CityDB::class)
    suspend fun insertHistoryItem(cityDB: CityDB)

    @Query("DELETE FROM CityDB WHERE id IN (SELECT id FROM CityDB ORDER BY id LIMIT 1)")
    suspend fun deleteHistoryItem()
}

@Database(entities = [CityDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dbDao(): DAO
}