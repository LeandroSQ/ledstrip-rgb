package quevedo.soares.leandro.automation.util

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import quevedo.soares.leandro.automation.R
import quevedo.soares.leandro.automation.view.shortcut.ShortcutActivity

object GoogleShortcutUtil {

	fun define(context: Context) {
		ShortcutInfoCompat.Builder(context, "power_toggle")
			.setIntent(Intent(context, ShortcutActivity::class.java).apply {
				action = Intent.ACTION_VIEW
				putExtra("action", "power_toggle")
			})
			.setIcon(IconCompat.createWithResource(context, R.drawable.ic_power))
			.setShortLabel(context.getString(R.string.shortcut_power_short))
			.setLongLabel(context.getString(R.string.shortcut_power_long))
			.setDisabledMessage(context.getString(R.string.shortcut_power_disabled))
			.build()
			.let {
				ShortcutManagerCompat.pushDynamicShortcut(context, it)
			}
	}


}