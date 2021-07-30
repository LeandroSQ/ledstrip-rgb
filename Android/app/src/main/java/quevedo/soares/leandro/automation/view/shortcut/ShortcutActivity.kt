package quevedo.soares.leandro.automation.view.shortcut

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import quevedo.soares.leandro.automation.service.PowerToggleService

class ShortcutActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		handleShortcutAction()
	}

	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)

		handleShortcutAction()
	}

	private fun handleShortcutAction() {
		if (intent.hasExtra("action")) {
			WorkManager.getInstance(this)
				.beginWith(OneTimeWorkRequest.Builder(PowerToggleService::class.java).build())
				.enqueue()
		}

		finishAffinity()
	}

}