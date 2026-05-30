package com.example.data.repository

import com.example.data.database.BabuuuuDao
import com.example.data.model.Chronic
import com.example.data.model.Echo
import kotlinx.coroutines.flow.Flow

class BabuuuuRepository(private val dao: BabuuuuDao) {

    val allChronicles: Flow<List<Chronic>> = dao.getAllChronicles()

    fun getEchoesForChronic(chronicId: Int): Flow<List<Echo>> = dao.getEchoesForChronic(chronicId)

    suspend fun insertChronic(chronic: Chronic) {
        dao.insertChronic(chronic)
    }

    suspend fun updateChronic(chronic: Chronic) {
        dao.updateChronic(chronic)
    }

    suspend fun addComment(echo: Echo) {
        dao.addCommentAndUpdateCount(echo)
    }

    suspend fun clearAll() {
        dao.clearAllChronicles()
    }
}
