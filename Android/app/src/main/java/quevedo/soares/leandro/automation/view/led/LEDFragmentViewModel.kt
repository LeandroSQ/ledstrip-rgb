package quevedo.soares.leandro.automation.view.led

import androidx.lifecycle.MutableLiveData
import quevedo.soares.leandro.automation.R
import quevedo.soares.leandro.automation.generic.GenericViewModel
import quevedo.soares.leandro.automation.network.repository.LEDRepository
import quevedo.soares.leandro.automation.view.led.adapter.LEDStyleItem


class LEDFragmentViewModel(private val repository: LEDRepository) : GenericViewModel() {

	val styles = listOf(
		LEDStyleItem("Rainbow cycle", R.drawable.shape_rounded_gradient_1),
		LEDStyleItem("Rainbow", R.drawable.shape_rounded_gradient_2),
		LEDStyleItem("Solid color", R.drawable.shape_rounded_gradient_3),
		LEDStyleItem("Knight rider", R.drawable.shape_rounded_gradient_4),
		LEDStyleItem("Sparkle", R.drawable.shape_rounded_gradient_5),
		LEDStyleItem("Pulse", R.drawable.shape_rounded_gradient_6),
		LEDStyleItem("Fire", R.drawable.shape_rounded_gradient_7),
	)

	var brightness = MutableLiveData<Int>()
	var speed = MutableLiveData<Float>()

	fun setAnimation(value: Int) = prepare { repository.setAnimation(value) }

	fun setBrightness(value: Int) = prepare {
		repository.setBrightness(value).apply {
			if (isSuccessful) getBrightness()
		}
	}

	fun getBrightness() = prepare {
		repository.getBrightness().apply {
			if (isSuccessful && body()?.toIntOrNull() != null) {
				brightness.postValue(body()?.toInt())
			}
		}
	}

	fun setSpeed(value: Int) = prepare { repository.setSpeed(value) }

	fun getSpeed() = prepare {
		repository.getSpeed().apply {
			if (isSuccessful && body()?.toFloatOrNull() != null) {
				speed.postValue(body()?.toFloat())
			}
		}
	}

	fun setColor(value: String) = prepare { repository.setColor(value) }

}