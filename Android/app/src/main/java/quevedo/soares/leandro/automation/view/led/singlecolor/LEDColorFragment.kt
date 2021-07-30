package quevedo.soares.leandro.automation.view.led.singlecolor

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apandroid.colorwheel.gradientseekbar.currentColorAlpha
import com.apandroid.colorwheel.gradientseekbar.setBlackToColor
import okhttp3.internal.toHexString
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import quevedo.soares.leandro.automation.databinding.FragmentLedColorBinding
import quevedo.soares.leandro.automation.view.led.LEDFragmentViewModel

class LEDColorFragment : Fragment() {

	private lateinit var binding: FragmentLedColorBinding
	private val viewModel: LEDFragmentViewModel by sharedViewModel()
	private val navController by lazy { findNavController() }
	private var currentColor: Int = 0

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
			isLoading.observe(viewLifecycleOwner, {
				// Inverse because of the GroupDispatcher behaviour
				binding.idLoader.visibility = if (!it) View.VISIBLE else View.INVISIBLE
			})
		}
	}

	private fun setupColorWheel() {
		binding.idColorWheel.colorChangeListener = this::onColorChange
		binding.idColorLuminosity.colorChangeListener = this::onBrightnessChange
	}

	private fun onColorChange(color: Int) {
		setPreviewColor(binding.idColorLuminosity.offset, color)
		binding.idColorLuminosity.setBlackToColor(color)
	}

	private fun onBrightnessChange(alpha: Float, color: Int) {
		setPreviewColor(alpha, color)
	}

	private fun setupColorPreview () {
		binding.idColorPreview.setOnClickListener {
			this.viewModel.setColor(getStringHex(currentColor))
		}
	}

	private fun setPreviewColor(alpha: Float, color: Int) {
		currentColor = getArgb(alpha, color)

		binding.idColorPreview.background = binding.idColorPreview.background.mutate().apply {
			setTint(currentColor)
		}
	}

	private fun getArgb(alpha: Float, @ColorInt color: Int): Int {
		val a = (alpha * 255).toInt()

		// Bit shift to the left
		return (a shl 24) or color
	}

	private fun getStringHex(@ColorInt color: Int): String {
		fun pad(x: Int) = x.toString(16).padStart(2, '0')

		val alpha = Color.alpha(color) / 255f
		val red = (Color.red(color) * alpha).toInt()
		val green = (Color.green(color) * alpha).toInt()
		val blue = (Color.blue(color) * alpha).toInt()

		return pad(red) + pad(green) + pad(blue)
	}

}