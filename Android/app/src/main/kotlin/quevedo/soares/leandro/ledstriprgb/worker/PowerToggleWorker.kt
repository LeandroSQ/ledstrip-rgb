package quevedo.soares.leandro.ledstriprgb.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.network.repository.LEDRepository
import quevedo.soares.leandro.ledstriprgb.util.NotificationUtil

class PowerToggleWorker(private val repository: LEDRepository, context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

	private val notificationId = 0

	private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

	override fun doWork(): Result {
		try {
			// Create the notification and start the service
			val notification = NotificationUtil.create(
				context = applicationContext,
				workerId = this.id,
				channelId = R.string.notification_channel_background_id,
				channelName = R.string.notification_channel_background_name,
				title = R.string.worker_notification_title,
				textResource = R.string.worker_notification_text
			)
			val foregroundInfo = ForegroundInfo(notificationId, notification)
			setForegroundAsync(foregroundInfo)

			// Calls the API
			toggleLEDState()
		} catch (e: Exception) {
			e.printStackTrace()

			return Result.failure(
				Data.Builder()
					.putString("message", e.message)
					.build()
			)
		} finally {
			// Dismisses the notification
			notificationManager.cancel(notificationId)
		}

		return Result.success()
	}

	override fun onStopped() {
		super.onStopped()

		// Dismisses the notification
		notificationManager.cancel(notificationId)
	}

	private fun toggleLEDState() = runBlocking {
		repository.togglePower()
	}

}