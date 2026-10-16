package com.windingmaster.app.domain.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "motor_profiles")
data class MotorProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val motorType: String,
    val slots: Int,
    val poles: Int,
    val windingType: String,
    val layerType: String,
    val coilPitch: String,
    val notes: String = ""
)
