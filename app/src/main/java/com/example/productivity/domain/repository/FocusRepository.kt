package com.example.productivity.domain.repository

import com.example.productivity.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface FocusRepository {
    fun getSessions(): Flow<List<FocusSession>>
    suspend fun saveSession(session: FocusSession)
}
