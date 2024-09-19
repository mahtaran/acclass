package nl.utwente.smartspaces.acclass.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.TimeSource

class ClassificationViewModel : ViewModel() {
	private val timeSource = TimeSource.Monotonic

	private val _uiState = MutableStateFlow(ClassificationUiState(timeSource.markNow()))
	val uiState = _uiState.asStateFlow()
}
