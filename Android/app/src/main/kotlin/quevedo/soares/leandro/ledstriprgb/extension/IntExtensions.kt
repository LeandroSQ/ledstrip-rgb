package quevedo.soares.leandro.ledstriprgb.extension

import android.graphics.Color

private inline fun pad(x: Int) = x.toString(16).padStart(2, '0')

fun Int.toColorHex(): String {
	val alpha = Color.alpha(this) / 255f
	val red = (Color.red(this) * alpha).toInt()
	val green = (Color.green(this) * alpha).toInt()
	val blue = (Color.blue(this) * alpha).toInt()

	return pad(red) + pad(green) + pad(blue)
}