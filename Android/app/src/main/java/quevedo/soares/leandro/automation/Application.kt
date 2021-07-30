package quevedo.soares.leandro.automation

import quevedo.soares.leandro.automation.util.GoogleShortcutUtil

class Application : android.app.Application() {

	override fun onCreate() {
		super.onCreate()

		setupKoin(this)
		GoogleShortcutUtil.define(this)
	}

}