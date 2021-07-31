package quevedo.soares.leandro.ledstriprgb.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import quevedo.soares.leandro.ledstriprgb.R
import java.util.*

object NotificationUtil {

	fun create(context: Context, workerId: UUID, @StringRes channelId: Int, @StringRes channelName: Int, @StringRes title: Int, text: String? = null, @StringRes textResource: Int? = null): Notification {
		// Assure that at least one text was provided
		if (text == null && textResource == null) throw Exception("Text or TextResource null! At least one should be provided.")

		// Get from 'text' or 'textResource'
		val notificationText = text ?: context.getString(textResource!!)

		// Create a cancelable pending intent
		val intent = WorkManager.getInstance(context).createCancelPendingIntent(workerId)

		// Assures that the notification channel exists
		createNotificationChannel(context, context.getString(channelId), context.getString(channelName))

		// Return the notification
		return NotificationCompat.Builder(context, context.getString(channelId))
			.setContentTitle(context.getString(title))
			.setTicker(context.getString(title))
			.setContentText(notificationText)
			.setSmallIcon(R.drawable.ic_notification)
			.setOngoing(true)
			.addAction(android.R.drawable.ic_delete, context.getString(R.string.action_cancel), intent)
			.build()
	}

	@SuppressLint("ObsoleteSdkInt")
	private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
		// Ignore for older versions
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		// Gather system service
		val notificationManager = context.getSystemService(NotificationManager::class.java)

		// Create the notification channel
		val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN)
		notificationManager.createNotificationChannel(channel)
	}

}