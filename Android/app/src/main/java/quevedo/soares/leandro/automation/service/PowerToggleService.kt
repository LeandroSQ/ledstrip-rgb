package quevedo.soares.leandro.automation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.runBlocking
import quevedo.soares.leandro.automation.R
import quevedo.soares.leandro.automation.network.repository.LEDRepository
import kotlin.coroutines.CoroutineContext

class PowerToggleService(private val repository: LEDRepository, context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

	private val notificationChannelName = "Background operations"
	private val notificationChannelId = "background_operations"
	private val notificationTitle = "Running background operation"
	private val notificationCancelLabel = "Cancel operation"
	private val notificationId = 0

	private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

	override fun doWork(): Result {
		try {
			// Create the notification and start the service
			val foregroundInfo = ForegroundInfo(notificationId, createNotification("Fetching current state..."))
			setForegroundAsync(foregroundInfo)

			// Fetch the current LED state from the API
			val brightness = fetchCurrentLEDState()

			if (isStopped) return Result.failure()

			// Update the LED state communicating with the API
			if (brightness <= 0) {
				// Update the notification text
				notificationManager.notify(notificationId, createNotification("Turning ON..."))

				updateCurrentLEDState(128)
			} else {
				// Update the notification text
				notificationManager.notify(notificationId, createNotification("Turning OFF..."))

				updateCurrentLEDState(0)
			}
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

	private fun fetchCurrentLEDState() = runBlocking {
		val response = repository.getBrightness()

		if (response.isSuccessful && !response.body().isNullOrEmpty()) return@runBlocking response.body()!!.toInt()

		throw Exception(response.message())
	}

	private fun updateCurrentLEDState(brightness: Int) = runBlocking {
		repository.setBrightness(brightness)
	}

	private fun createNotification(text: String): Notification {
		val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

		createNotificationChannel()

		return NotificationCompat.Builder(applicationContext, notificationChannelId)
			.setContentTitle(notificationTitle)
			.setTicker(notificationTitle)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_notification)
			.setOngoing(true)
			.addAction(android.R.drawable.ic_delete, notificationCancelLabel, intent)
			.build()
	}

	@SuppressLint("ObsoleteSdkInt")
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
		val channel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_MIN)

		notificationManager.createNotificationChannel(channel)
	}

}