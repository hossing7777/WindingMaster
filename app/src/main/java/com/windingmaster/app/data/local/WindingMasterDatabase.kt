package com.windingmaster.app.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.windingmaster.app.domain.model.MotorProfile
@Database(entities = [MotorProfile::class], version = 1, exportSchema = false)
abstract class WindingMasterDatabase : RoomDatabase() {
    abstract fun motorProfileDao(): MotorProfileDao
}
