package com.example.productivity.data.repository

import com.example.productivity.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeFocusRepository @Inject constructor() : com.example.productivity.domain.repository.FocusRepository {
    
    private val _sessions = MutableStateFlow<List<FocusSession>>(emptyList())

    override fun getSessions(): Flow<List<FocusSession>> = _sessions

    override suspend fun saveSession(session: FocusSession) {
        val current = _sessions.value.toMutableList()
        current.add(session)
        _sessions.value = current
    }
}
