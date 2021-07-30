package quevedo.soares.leandro.automation.extension

import android.graphics.PointF
import android.graphics.Rect

fun Rect.contains(point: PointF) = this.contains(point.x.toInt(), point.y.toInt())
