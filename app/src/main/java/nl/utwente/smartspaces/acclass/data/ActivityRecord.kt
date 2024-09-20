package nl.utwente.smartspaces.acclass.data

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class ActivityRecord(
	val activity: Activity,
	val location: LatLng,
	val timestamp: LocalDateTime
)
