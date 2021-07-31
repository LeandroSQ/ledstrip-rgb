package quevedo.soares.leandro.ledstriprgb.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.databinding.FragmentLedBinding
import quevedo.soares.leandro.ledstriprgb.view.home.adapter.LEDEffectAdapter

class HomeFragment : Fragment() {

	private lateinit var binding: FragmentLedBinding
	private val viewModel: HomeFragmentViewModel by sharedViewModel()
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
		this.doInitialRequests()
	}

	private fun setupRecyclerView() {
		LEDEffectAdapter(binding.idRecyclerView, viewModel.animationList) { selected ->
			viewModel.setAnimation(selected.animation.id)
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

			isAnyRequestAlive.observe(viewLifecycleOwner) {
				// Inverse because of the GroupDispatcher behaviour
				binding.idLoader.apply {
					visibility = if (!it) View.VISIBLE else View.GONE
				}
			}

			state.observe(viewLifecycleOwner) {
				when (it) {

					is HomeFragmentViewModel.ViewState.Error -> {
						// TODO: Show errors
					}

					is HomeFragmentViewModel.ViewState.FetchBrightness -> {
						binding.idBrightnessSlider.setValueAnimated(it.value.toFloat())
					}

					is HomeFragmentViewModel.ViewState.FetchSpeed -> {
						binding.idSpeedSlider.setValueAnimated(it.value)
					}

				}
			}

		}
	}

	private fun doInitialRequests() {
		this.viewModel.apply {
			getBrightness()
			getSpeed()
		}
	}

}