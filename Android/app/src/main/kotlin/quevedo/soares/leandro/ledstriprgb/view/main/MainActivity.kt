package quevedo.soares.leandro.ledstriprgb.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val navController by lazy { findNavController(R.id.idFragmentContainerView) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		inflateLayout()
	}

	private fun inflateLayout() {
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}

}