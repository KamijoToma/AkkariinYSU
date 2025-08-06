package cn.edu.ysu.ciallo.studentinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.ysu.ciallo.ysu.StudentBaseInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentInfoViewModel(
    private val repository: StudentInfoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentInfoUiState>(StudentInfoUiState.Loading)
    val uiState: StateFlow<StudentInfoUiState> = _uiState.asStateFlow()

    fun loadStudentInfo() {
        viewModelScope.launch {
            _uiState.value = StudentInfoUiState.Loading
            when (val result = repository.getStudentBaseInfo()) {
                is StudentInfoResult.Success -> {
                    _uiState.value = StudentInfoUiState.Success(result.studentInfo)
                }

                is StudentInfoResult.Failure -> {
                    _uiState.value = StudentInfoUiState.Error(result.error.toString())
                }
            }
        }
    }
}

sealed class StudentInfoUiState {
    object Loading : StudentInfoUiState()
    data class Success(val studentInfo: StudentBaseInfo) : StudentInfoUiState()
    data class Error(val message: String) : StudentInfoUiState()
}
