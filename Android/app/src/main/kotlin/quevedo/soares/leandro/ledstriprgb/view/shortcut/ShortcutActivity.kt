package quevedo.soares.leandro.ledstriprgb.view.shortcut

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import quevedo.soares.leandro.ledstriprgb.worker.PowerToggleWorker

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
				.beginWith(OneTimeWorkRequest.Builder(PowerToggleWorker::class.java).build())
				.enqueue()
		}

		finishAffinity()
	}

}