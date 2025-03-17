package com.example.autokiosk.data.local

import androidx.room.*
import com.example.autokiosk.domain.models.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM cards")
    fun getAllCards(): Flow<List<Card>>
}
