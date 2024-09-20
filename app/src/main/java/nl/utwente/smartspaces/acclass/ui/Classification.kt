package nl.utwente.smartspaces.acclass.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import nl.utwente.smartspaces.acclass.data.DEFAULT_LOCATION
import nl.utwente.smartspaces.acclass.data.PREDICTION_INTERVAL
import weka.classifiers.Classifier
import java.io.ObjectInputStream
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Classification(
	padding: PaddingValues,
	viewModel: ClassificationViewModel = viewModel()
) {
	val context = LocalContext.current
	val configuration = LocalConfiguration.current

	val classifier = ObjectInputStream(context.assets.open("classifier.model")).use {
		it.readObject() as Classifier
	}
	viewModel.initClassifier(classifier)

	val permissionState = rememberMultiplePermissionsState(
		listOf(
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
	)

	if (permissionState.allPermissionsGranted) {
		when (configuration.orientation) {
			Configuration.ORIENTATION_PORTRAIT -> {
				Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
					Visualisation(padding)
				}
			}

			Configuration.ORIENTATION_LANDSCAPE -> {
				Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
					Visualisation(padding)
				}
			}

			else -> {
				Text(
					text = "Unsupported orientation",
					textAlign = TextAlign.Center
				)
			}
		}
	} else {
		Column(
			modifier = Modifier
				.padding(32.dp)
				.fillMaxSize()
				.wrapContentHeight(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Location permissions are needed to localise your device",
				textAlign = TextAlign.Center
			)
			Button(
				onClick = { permissionState.launchMultiplePermissionRequest() }
			) {
				Text("Request permission")
			}
		}
	}
}

@Composable
fun Visualisation(
	padding: PaddingValues,
	viewModel: ClassificationViewModel = viewModel()
) {
	val context = LocalContext.current
	val uiState by viewModel.uiState.collectAsState()

	val locationClient = LocationServices.getFusedLocationProviderClient(context)
	val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

	val sensorListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				event?.let {
					when (it.sensor.type) {
						Sensor.TYPE_ACCELEROMETER -> {
							viewModel.updateAccelerometer(it.values)
						}

						Sensor.TYPE_GYROSCOPE -> {
							viewModel.updateGyroscope(it.values)
						}

						Sensor.TYPE_MAGNETIC_FIELD -> {
							viewModel.updateMagnetometer(it.values)
						}

						Sensor.TYPE_LINEAR_ACCELERATION -> {
							viewModel.updateLinearAcceleration(it.values)
						}
					}
				}
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
				// Do nothing
			}
		}
	}

	DisposableEffect(Unit) {
		sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
			sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
		}

		sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
			sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
		}

		sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
			sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
		}

		sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
			sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
		}

		onDispose {
			sensorManager.unregisterListener(sensorListener)
		}
	}

	LaunchedEffect(Unit) {
		while (true) {
			if (
				context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED
			) {
				locationClient.getCurrentLocation(
					CurrentLocationRequest.Builder().setDurationMillis(
						PREDICTION_INTERVAL.inWholeMilliseconds
					).build(), null
				).addOnSuccessListener {
					viewModel.predictActivity(LatLng(it.latitude, it.longitude))
				}
				delay(PREDICTION_INTERVAL)
			}
		}
	}

	val cameraPositionState = rememberCameraPositionState {
		position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 15f)
	}

	Column {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = uiState.lastActivity?.name ?: "UNKNOWN",
			)
			Text(
				text = uiState.lastUpdate.format(DateTimeFormatter.ISO_LOCAL_TIME)
			)
		}

		GoogleMap(
			modifier = Modifier.fillMaxSize(),
			cameraPositionState = cameraPositionState,
			contentPadding = padding,
			properties = MapProperties(isMyLocationEnabled = true)
		) {
			uiState.activityHistory.forEach { activity ->
				Marker(
					state = rememberMarkerState(position = activity.location),
					title = activity.activity.name,
					snippet = activity.timestamp.format(DateTimeFormatter.ISO_LOCAL_TIME),
				)
			}

			Marker(
				state = rememberMarkerState(position = LatLng(52.2383, 6.8507)),
				title = "University of Twente",
				snippet = "The most beautiful campus in the Netherlands",
			)
		}
	}
}
