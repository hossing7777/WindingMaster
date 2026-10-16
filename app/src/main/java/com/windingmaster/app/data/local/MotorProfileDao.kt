package com.windingmaster.app.data.local
import androidx.room.*
import com.windingmaster.app.domain.model.MotorProfile
import kotlinx.coroutines.flow.Flow
@Dao
interface MotorProfileDao {
    @Query("SELECT * FROM motor_profiles ORDER BY id DESC")
    fun getAllProfiles(): Flow<List<MotorProfile>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: MotorProfile): Long
    @Delete
    suspend fun deleteProfile(profile: MotorProfile)
}
