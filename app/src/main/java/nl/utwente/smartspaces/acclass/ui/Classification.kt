package nl.utwente.smartspaces.acclass.ui

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Classification(
	padding: PaddingValues,
	viewModel: ClassificationViewModel = viewModel()
) {
	val context = LocalContext.current
	val configuration = LocalConfiguration.current

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
					Text(
						text = "Hello, world!",
						textAlign = TextAlign.Center
					)
				}
			}

			Configuration.ORIENTATION_LANDSCAPE -> {
				Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
					Text(
						text = "Hello, world!",
						textAlign = TextAlign.Center
					)
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
				text = "Location and Bluetooth permissions are needed to localise your device",
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
