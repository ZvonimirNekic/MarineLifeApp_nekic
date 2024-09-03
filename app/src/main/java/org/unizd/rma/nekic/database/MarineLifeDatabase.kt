package org.unizd.rma.nekic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.unizd.rma.nekic.dao.MarineLifeDao
import org.unizd.rma.nekic.models.MarineLife


@Database(
    entities = [MarineLife::class],
    version = 6,
)
@TypeConverters(org.unizd.rma.nekic.converter.TypeConverter::class)
abstract class MarineLifeDatabase : RoomDatabase() {

    abstract val marineLifeDao : MarineLifeDao

    companion object {

        private val migration_5_6 = object : Migration(5,6){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `MarineLife_new` (" +
                        "`marineLifeId` TEXT NOT NULL, " +
                        "`color` TEXT NOT NULL, " +
                        "`date` INTEGER NOT NULL, " +
                        "`depth` TEXT NOT NULL, " +
                        "`marineurl` TEXT NOT NULL, " +
                        "`imageUri` TEXT NOT NULL, " +
                        "`typeofmarine` TEXT NOT NULL, " +
                        "PRIMARY KEY(`marineLifeId`))")

                database.execSQL("INSERT INTO `MarineLife_new` (`marineLifeId`, `color`, `date`, `depth`, `marineurl`, `imageUri`, `typeofmarine`) " +
                        "SELECT `marineLifeId`, '', `date`, '', '', `imageUri`, '' FROM `MarineLife`")

                database.execSQL("DROP TABLE `MarineLife`")

                database.execSQL("ALTER TABLE `MarineLife_new` RENAME TO `MarineLife`")

            }
        }


        @Volatile
        private var INSTANCE: MarineLifeDatabase? = null
        fun getInstance(context: Context): MarineLifeDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MarineLifeDatabase::class.java,
                    "marineLife_db"

                ).addMigrations(migration_5_6)
                    .build().also {
                    INSTANCE = it
                }
            }

        }
    }

}