package quevedo.soares.leandro.ledstriprgb.data.model

import androidx.annotation.DrawableRes
import quevedo.soares.leandro.ledstriprgb.data.enum.LEDEffect

data class LEDEffectItem(
	var animation: LEDEffect,
	@DrawableRes var background: Int
)