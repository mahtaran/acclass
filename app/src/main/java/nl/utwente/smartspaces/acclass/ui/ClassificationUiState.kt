package nl.utwente.smartspaces.acclass.ui

import nl.utwente.smartspaces.acclass.data.Activity
import nl.utwente.smartspaces.acclass.data.ActivityRecord
import nl.utwente.smartspaces.acclass.data.SlidingWindow
import nl.utwente.smartspaces.acclass.data.WINDOW_SIZE
import java.time.LocalDateTime

data class ClassificationUiState(
	val lastUpdate: LocalDateTime,
	val lastActivity: Activity? = null,
	val accelerometer: SlidingWindow = SlidingWindow(WINDOW_SIZE),
	val gyroscope: SlidingWindow = SlidingWindow(WINDOW_SIZE),
	val magnetometer: SlidingWindow = SlidingWindow(WINDOW_SIZE),
	val linearAcceleration: SlidingWindow = SlidingWindow(WINDOW_SIZE),
	val activityHistory: List<ActivityRecord> = emptyList()
)
