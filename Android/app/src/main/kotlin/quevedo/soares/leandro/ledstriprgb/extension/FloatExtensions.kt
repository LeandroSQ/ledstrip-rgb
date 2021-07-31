package quevedo.soares.leandro.ledstriprgb.extension

infix fun Float.clamp(range: ClosedFloatingPointRange<Float>): Float {
	return when {
		this < range.start -> range.start
		this > range.endInclusive -> range.endInclusive
		else -> this
	}
}
