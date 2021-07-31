package quevedo.soares.leandro.ledstriprgb.view.home.singlecolor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apandroid.colorwheel.gradientseekbar.setBlackToColor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quevedo.soares.leandro.ledstriprgb.databinding.FragmentLedColorBinding
import quevedo.soares.leandro.ledstriprgb.extension.toColorHex
import quevedo.soares.leandro.ledstriprgb.view.home.HomeFragmentViewModel

private val MINIMUM_IDLE_TIME_TO_UPDATE = 1000

class LEDColorFragment : Fragment() {

	private lateinit var binding: FragmentLedColorBinding
	private val viewModel: HomeFragmentViewModel by sharedViewModel()
	private val navController by lazy { findNavController() }

	private var currentColor: Int = 0

	private var lastUpdateRequestTime = -1L
	private var autoColorChangeHandler: Handler? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentLedColorBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		this.setupObservers()
		this.setupColorWheel()
		this.setupColorPreview()
	}

	private fun setupObservers() {
		this.viewModel.apply {

			isAnyRequestAlive.observe(viewLifecycleOwner, {
				// Inverse because of the GroupDispatcher behaviour
				binding.idLoader.visibility = if (!it) View.VISIBLE else View.INVISIBLE
			})

		}
	}

	private fun setupColorWheel() {
		binding.idColorWheel.colorChangeListener = this::onHueChange
		binding.idColorLuminosity.colorChangeListener = this::onBrightnessChange
	}

	private fun onHueChange(color: Int) {
		onColorChange(binding.idColorLuminosity.offset, color)
		binding.idColorLuminosity.setBlackToColor(color)
	}

	private fun onBrightnessChange(alpha: Float, color: Int) {
		onColorChange(alpha, color)
	}

	private fun setupColorPreview() {
		binding.idColorPreview.setOnClickListener {
			this.viewModel.setColor(currentColor.toColorHex())
		}
	}

	private fun onColorChange(alpha: Float, color: Int) {
		currentColor = getArgb(alpha, color)

		binding.idColorPreview.background = binding.idColorPreview.background.mutate().apply {
			setTint(currentColor)
		}

		scheduleAutoColorChangeTimer()
	}

	private fun scheduleAutoColorChangeTimer() {
		// Save the last update time
		this.lastUpdateRequestTime = System.currentTimeMillis()

		// Cancels all callbacks if any
		this.autoColorChangeHandler?.removeCallbacksAndMessages(null)

		// Schedule to one second in future to automatically set the new color to the API
		this.autoColorChangeHandler = Handler(Looper.getMainLooper()).apply {
			postDelayed({
				// Calculate the elapsed time between now and the last update
				val now = System.currentTimeMillis()
				val elapsed = now - lastUpdateRequestTime

				// If greater than the threshold
				if (elapsed > MINIMUM_IDLE_TIME_TO_UPDATE) {
					// Update the LED color
					viewModel.setColor(currentColor.toColorHex())
					lastUpdateRequestTime = now
				}
			}, 1000)
		}
	}

	private fun getArgb(alpha: Float, @ColorInt color: Int): Int {
		val a = (alpha * 255).toInt()

		// 0x00 - 0xFF ALPHA ALPHA
		// 0xFF00 - 0xFFFF RED RED
		// 0xFFFF00 - 0xFFFFFF GREEN GREEN
		// 0xFFFFFF00 - 0xFFFFFFFF BLUE BLUE
		return (a shl 24) or color
	}

}