package quevedo.soares.leandro.ledstriprgb.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val navController by lazy { findNavController(R.id.idFragmentContainerView) }
	private val toolbarConfiguration by lazy { AppBarConfiguration(navController.graph) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		inflateLayout()
	}

	override fun onStart() {
		super.onStart()

		setupNavigation()
	}

	private fun inflateLayout() {
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}

	private fun setupNavigation() {
		NavigationUI.setupActionBarWithNavController(this, this.navController, this.toolbarConfiguration)
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(this.toolbarConfiguration)
	}

}