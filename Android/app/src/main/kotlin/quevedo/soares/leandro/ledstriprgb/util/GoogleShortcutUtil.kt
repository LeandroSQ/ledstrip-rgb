package quevedo.soares.leandro.ledstriprgb.util

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

object GoogleShortcutUtil {

	/***
	 * Defines an app shortcut to be presented on the Application context menu
	 *
	 * @param targetActivity The activity to be started whenever the shortcut is pressed
	 ***/
	fun defineShortcut(context: Context, id: String, @DrawableRes icon: Int, @StringRes shortLabel: Int, @StringRes longLabel: Int, @StringRes disabledLabel: Int, targetActivity: Class<*>) {
		ShortcutInfoCompat.Builder(context, id)
			.setIntent(Intent(context, targetActivity).apply {
				action = Intent.ACTION_VIEW
				putExtra("action", id)
			})
			.setIcon(IconCompat.createWithResource(context, icon))
			.setShortLabel(context.getString(shortLabel))
			.setLongLabel(context.getString(longLabel))
			.setDisabledMessage(context.getString(disabledLabel))
			.build()
			.let {
				ShortcutManagerCompat.pushDynamicShortcut(context, it)
			}
	}

}