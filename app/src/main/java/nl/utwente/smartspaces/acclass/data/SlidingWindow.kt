package nl.utwente.smartspaces.acclass.data

import kotlin.time.TimeSource

typealias MonoTriple<T> = Triple<T, T, T>

class SlidingWindow(private val size: Int) {
	private val window: MutableList<MonoTriple<Double>> = ArrayList(size)
	private val timeSource = TimeSource.Monotonic
	private var lastUpdate = timeSource.markNow()

	val average: MonoTriple<Double>
		get() = MonoTriple(
			window.map { it.first }.average(),
			window.map { it.second }.average(),
			window.map { it.third }.average()
		)

	fun add(value: MonoTriple<Double>): SlidingWindow {
		if (lastUpdate + MEASURE_INTERVAL > timeSource.markNow()) {
			// Short circuit
			return this
		}

		if (window.size == size) {
			window.removeAt(0)
		}
		window.add(value)
		lastUpdate = timeSource.markNow()

		return this
	}

	fun add(values: FloatArray?): SlidingWindow {
		require(values != null) { "values must not be null" }
		require(values.size >= 3) { "values must have at least 3 elements" }

		return add(MonoTriple(values[0].toDouble(), values[1].toDouble(), values[2].toDouble()))
	}
}
