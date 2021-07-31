package quevedo.soares.leandro.ledstriprgb

import org.koin.core.KoinExperimentalAPI
import quevedo.soares.leandro.ledstriprgb.di.setupKoin
import quevedo.soares.leandro.ledstriprgb.util.GoogleShortcutUtil
import quevedo.soares.leandro.ledstriprgb.view.shortcut.ShortcutActivity

class Application : android.app.Application() {

	@KoinExperimentalAPI
	override fun onCreate() {
		super.onCreate()

		setupKoin(this)
		definePowerToggleShortcut()
	}

	private fun definePowerToggleShortcut() {
		GoogleShortcutUtil.defineShortcut(
			context = this,
			id = "power_toggle",
			icon = R.drawable.ic_power,
			shortLabel = R.string.shortcut_power_short,
			longLabel = R.string.shortcut_power_long,
			disabledLabel = R.string.shortcut_power_disabled,
			targetActivity = ShortcutActivity::class.java
		)
	}

}