package org.unizd.rma.nekic.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.unizd.rma.nekic.models.MarineLife


@Dao
interface MarineLifeDao {


   @Query("SELECT * FROM MarineLife ORDER BY date DESC")
    fun getMarineLifeList() : Flow<List<MarineLife>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarineLife(marineLife: MarineLife): Long

    @Delete
    suspend fun deleteMarineLife(marineLife: MarineLife) : Int

    @Query("DELETE FROM MarineLife WHERE marineLifeId == :marineLifeId")
    suspend fun deleteMarineLifeUsingId(marineLifeId: String) : Int

    @Update
    suspend fun updateMarineLife(marineLife: MarineLife): Int

    @Query("UPDATE MarineLife SET color = :color  , depth = :depth, imageUri = :imageUri   WHERE marineLifeId = :marineLifeId")
    suspend fun updateMarineLifePaticularField(marineLifeId:String,color:String, depth: String, imageUri: String): Int
}