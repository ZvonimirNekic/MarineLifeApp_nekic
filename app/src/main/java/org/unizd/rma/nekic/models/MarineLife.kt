package org.unizd.rma.nekic.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity()
data class MarineLife(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "marineLifeId")
    val id: String,
    val color: String,
    val date: Date,
    val depth : String,
    val imageUri: String,
    val typeOfMarine: String
)