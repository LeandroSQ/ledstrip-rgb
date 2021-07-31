package quevedo.soares.leandro.ledstriprgb.extension

import android.graphics.PointF
import android.graphics.Rect

fun Rect.contains(point: PointF) = this.contains(point.x.toInt(), point.y.toInt())
