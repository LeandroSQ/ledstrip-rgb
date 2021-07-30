package quevedo.soares.leandro.automation.view.main

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import quevedo.soares.leandro.automation.R
import quevedo.soares.leandro.automation.databinding.ActivityMainBinding
import quevedo.soares.leandro.automation.service.PowerToggleService

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val navController by lazy { findNavController(R.id.idFragmentContainerView) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		inflateLayout()
		setupBottomAppBar()
	}


	private fun inflateLayout() {
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}

	private fun setupBottomAppBar() {
		// Animate appearance of the bottom app bar and floating action button
		TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 2f, TranslateAnimation.RELATIVE_TO_SELF, 0f).apply {
			this.duration = 500
			this.interpolator = DecelerateInterpolator()

			binding.idBottomAppBar.startAnimation(this)
			binding.idFloatingActionButton.startAnimation(this)
		}

		// Set the menu listener
		binding.idBottomAppBar.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.action_led -> navController.navigate(R.id.LEDFragment)

				R.id.action_wake_on_lan -> navController.navigate(R.id.PCFragment)
			}

			true
		}
	}

}