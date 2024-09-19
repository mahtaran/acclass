package nl.utwente.smartspaces.acclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import nl.utwente.smartspaces.acclass.ui.Classification
import nl.utwente.smartspaces.acclass.ui.theme.AcclassTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			AcclassTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
					Classification(padding)
				}
			}
		}
	}
}
