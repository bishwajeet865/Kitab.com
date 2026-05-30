package com.example.data.database

import androidx.room.*
import com.example.data.model.Chronic
import com.example.data.model.Echo
import kotlinx.coroutines.flow.Flow

@Dao
interface BabuuuuDao {

    @Query("SELECT * FROM chronicles ORDER BY timestamp DESC")
    fun getAllChronicles(): Flow<List<Chronic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChronic(chronic: Chronic)

    @Update
    suspend fun updateChronic(chronic: Chronic)

    @Query("SELECT * FROM echoes WHERE chronicId = :chronicId ORDER BY timestamp ASC")
    fun getEchoesForChronic(chronicId: Int): Flow<List<Echo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEcho(echo: Echo)

    @Query("UPDATE chronicles SET commentsCount = commentsCount + 1 WHERE id = :chronicId")
    suspend fun incrementCommentsCount(chronicId: Int)

    @Transaction
    suspend fun addCommentAndUpdateCount(echo: Echo) {
        insertEcho(echo)
        incrementCommentsCount(echo.chronicId)
    }

    @Query("DELETE FROM chronicles")
    suspend fun clearAllChronicles()
}
