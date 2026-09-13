package com.example.data.local

import android.content.Context
import com.example.BuildConfig
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class StringListConverters {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value.isNullOrBlank()) return null
        return try {
            adapter.fromJson(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        if (list == null) return null
        return adapter.toJson(list)
    }
}

@Database(
    entities = [
        JobEntity::class,
        OrganisationEntity::class,
        SyncMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverters::class)
abstract class RecruitmentDatabase : RoomDatabase() {

    abstract fun jobDao(): JobDao
    abstract fun organisationDao(): OrganisationDao
    abstract fun syncMetadataDao(): SyncMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: RecruitmentDatabase? = null

        fun getInstance(context: Context): RecruitmentDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RecruitmentDatabase::class.java,
                    if (BuildConfig.DEBUG) "recruitment_database_debug.db" else "recruitment_database.db"
                )
                // Future schema versions must supply migrations, not erase cached data silently.
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
