package nl.utwente.smartspaces.acclass.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.utwente.smartspaces.acclass.data.Activity
import nl.utwente.smartspaces.acclass.data.ActivityRecord
import nl.utwente.smartspaces.acclass.data.MonoTriple
import weka.classifiers.Classifier
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instance
import weka.core.Instances
import java.time.LocalDateTime

class ClassificationViewModel : ViewModel() {
	private lateinit var classifier: Classifier

	private val _uiState = MutableStateFlow(ClassificationUiState(LocalDateTime.now()))
	val uiState = _uiState.asStateFlow()

	fun initClassifier(classifier: Classifier) {
		this.classifier = classifier
	}

	fun updateAccelerometer(values: FloatArray?) {
		_uiState.update { currentState ->
			currentState.copy(
				accelerometer = currentState.accelerometer.add(values)
			)
		}
	}

	fun updateGyroscope(values: FloatArray?) {
		_uiState.update { currentState ->
			currentState.copy(
				gyroscope = currentState.gyroscope.add(values)
			)
		}
	}

	fun updateMagnetometer(values: FloatArray?) {
		_uiState.update { currentState ->
			currentState.copy(
				magnetometer = currentState.magnetometer.add(values)
			)
		}
	}

	fun updateLinearAcceleration(values: FloatArray?) {
		_uiState.update { currentState ->
			currentState.copy(
				linearAcceleration = currentState.linearAcceleration.add(values)
			)
		}
	}

	fun predictActivity(location: LatLng) {
		val attributes = arrayListOf(
			Attribute("Left_pocket_Ax"),
			Attribute("Left_pocket_Ay"),
			Attribute("Left_pocket_Az"),
			Attribute("Left_pocket_Lx"),
			Attribute("Left_pocket_Ly"),
			Attribute("Left_pocket_Lz"),
			Attribute("Left_pocket_Gx"),
			Attribute("Left_pocket_Gy"),
			Attribute("Left_pocket_Gz"),
			Attribute("Left_pocket_Mx"),
			Attribute("Left_pocket_My"),
			Attribute("Left_pocket_Mz"),
			Attribute("Activity", Activity.entries.map { it.name.lowercase() })
		)

		val dataset = Instances("Activity", attributes, 0).apply {
			setClassIndex(attributes.size - 1)
		}

		val instance = DenseInstance(dataset.numAttributes()).apply {
			setValues(0, _uiState.value.accelerometer.average)
			setValues(3, _uiState.value.linearAcceleration.average)
			setValues(6, _uiState.value.gyroscope.average)
			setValues(9, _uiState.value.magnetometer.average)

			setDataset(dataset)
		}

		val predictionIndex = classifier.classifyInstance(instance).toInt()
		val prediction = dataset.classAttribute().value(predictionIndex)
		val now = LocalDateTime.now()
		val activity = Activity.valueOf(prediction.uppercase())

		Log.d("ClassificationViewModel", "predictActivity: $activity")

		_uiState.update { currentState ->
			if (currentState.lastActivity == activity) {
				currentState
			} else {
				currentState.copy(
					lastUpdate = now,
					lastActivity = activity,
					activityHistory = currentState.activityHistory
						+ ActivityRecord(activity, location, now)
				)
			}
		}
	}

	private fun Instance.setValues(offset: Int, values: MonoTriple<Double>) {
		setValue(offset, values.first)
		setValue(offset + 1, values.second)
		setValue(offset + 2, values.third)
	}
}
