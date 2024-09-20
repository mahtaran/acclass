package nl.utwente.smartspaces.acclass.data

import com.google.android.gms.maps.model.LatLng
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

const val WINDOW_SIZE = 32

val MEASURE_INTERVAL = 200.milliseconds
val PREDICTION_INTERVAL = 1.seconds

val DEFAULT_LOCATION = LatLng(52.2383, 6.8507)
