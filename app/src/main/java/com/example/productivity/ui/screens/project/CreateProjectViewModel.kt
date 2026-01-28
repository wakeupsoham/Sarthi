package com.example.productivity.ui.screens.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.data.repository.FakeProjectRepository
import com.example.productivity.domain.model.Milestone
import com.example.productivity.domain.model.Project
import com.example.productivity.domain.model.ProjectType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateProjectViewModel @Inject constructor(
    private val projectRepository: FakeProjectRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _type = MutableStateFlow(ProjectType.ACADEMIC)
    val type = _type.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onTypeChange(newType: ProjectType) {
        _type.value = newType
    }

    // AI Mock Logic: Generates milestones based on project name
    fun generateAiMilestones() {
        // In a real app, this would call an LLM. Here we simulate it.
        // We don't need to expose this as state for this quick prototype, 
        // we'll just auto-add them on save for now or assume user adds them.
        // For simplicity in this step, let's keep it manual or simple.
    }

    fun saveProject(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val newProject = Project(
                name = _name.value,
                description = "New Project",
                endDate = LocalDate.now().plusMonths(1),
                type = _type.value,
                milestones = listOf(
                    Milestone(title = "Kickoff", deadline = LocalDate.now().plusDays(1)),
                    Milestone(title = "Final Review", deadline = LocalDate.now().plusMonths(1))
                )
            )
            projectRepository.saveProject(newProject)
            onSuccess()
        }
    }
}
