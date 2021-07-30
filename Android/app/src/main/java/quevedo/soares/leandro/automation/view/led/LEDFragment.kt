package quevedo.soares.leandro.automation.view.led

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quevedo.soares.leandro.automation.databinding.FragmentLedBinding
import quevedo.soares.leandro.automation.view.led.adapter.LEDStyleAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import quevedo.soares.leandro.automation.R

class LEDFragment : Fragment() {

	private lateinit var binding: FragmentLedBinding
	private val viewModel: LEDFragmentViewModel by sharedViewModel()
	private val navController by lazy { findNavController() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentLedBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		this.setupRecyclerView()
		this.setupButtons()
		this.setupSliders()
		this.setupObservers()
		this.fetchDataFromApi()
	}

	private fun setupRecyclerView() {
		LEDStyleAdapter(binding.idRecyclerView, viewModel.styles) { item ->
			when(item.text) {
				"Rainbow cycle" -> {
					viewModel.setAnimation(0)
				}

				"Rainbow" -> {
					viewModel.setAnimation(1)
				}

				"Solid color" -> {
					viewModel.setAnimation(2)
				}

				"Knight rider" -> {
					viewModel.setAnimation(3)
				}

				"Sparkle" -> {
					viewModel.setAnimation(4)
				}

				"Pulse" -> {
					viewModel.setAnimation(5)
				}

				"Fire" -> {
					viewModel.setAnimation(6)
				}
			}
		}
	}

	private fun setupButtons() {
		this.binding.idButtonPowerOn.setOnClickListener {
			viewModel.setBrightness(127)
		}

		this.binding.idButtonColor.setOnClickListener {
			navController.navigate(R.id.action_LEDFragment_to_LEDColorFragment)
		}

		this.binding.idButtonPowerOff.setOnClickListener {
			viewModel.setBrightness(0)
		}
	}

	private fun setupSliders() {
		this.binding.idBrightnessSlider.onChangeListener = {
			viewModel.setBrightness(it.toInt())
		}

		this.binding.idSpeedSlider.onChangeListener = {
			viewModel.setSpeed(it.toInt())
		}
	}

	private fun setupObservers() {
		this.viewModel.apply {

			brightness.observe(viewLifecycleOwner, {
				binding.idBrightnessSlider.setValueAnimated(it.toFloat())
			})

			speed.observe(viewLifecycleOwner, {
				binding.idSpeedSlider.setValueAnimated(it.toFloat())
			})

			isLoading.observe(viewLifecycleOwner, {
				// Inverse because of the GroupDispatcher behaviour
				binding.idLoader.apply {
					visibility = if (!it) View.VISIBLE else View.GONE
				}
			})

		}
	}

	private fun fetchDataFromApi() {
		this.viewModel.apply {
			getBrightness()
			getSpeed()
		}
	}

}