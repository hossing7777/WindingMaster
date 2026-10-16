package com.windingmaster.app.di
import android.content.Context
import androidx.room.Room
import com.windingmaster.app.data.local.MotorProfileDao
import com.windingmaster.app.data.local.WindingMasterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WindingMasterDatabase {
        return Room.databaseBuilder(context, WindingMasterDatabase::class.java, "windingmaster_database").fallbackToDestructiveMigration().build()
    }
    @Provides
    fun provideMotorProfileDao(database: WindingMasterDatabase): MotorProfileDao = database.motorProfileDao()
}
