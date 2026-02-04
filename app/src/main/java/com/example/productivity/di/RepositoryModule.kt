package com.example.productivity.di

import com.example.productivity.data.repository.FakeProjectRepository
import com.example.productivity.data.repository.FakeTaskRepository
import com.example.productivity.data.repository.FakeFocusRepository
import com.example.productivity.domain.repository.ProjectRepository
import com.example.productivity.domain.repository.TaskRepository
import com.example.productivity.domain.repository.FocusRepository
import com.example.productivity.domain.repository.GeminiRepository
import com.example.productivity.data.repository.GeminiRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        fakeTaskRepository: FakeTaskRepository
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        fakeProjectRepository: FakeProjectRepository
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindFocusRepository(
        fakeFocusRepository: FakeFocusRepository
    ): FocusRepository

    @Binds
    @Singleton
    abstract fun bindGeminiRepository(
        geminiRepositoryImpl: GeminiRepositoryImpl
    ): GeminiRepository
}
