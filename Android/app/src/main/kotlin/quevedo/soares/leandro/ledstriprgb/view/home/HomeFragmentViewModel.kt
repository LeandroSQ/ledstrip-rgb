package quevedo.soares.leandro.ledstriprgb.view.home

import androidx.lifecycle.MutableLiveData
import quevedo.soares.leandro.ledstriprgb.R
import quevedo.soares.leandro.ledstriprgb.data.enum.LEDEffect
import quevedo.soares.leandro.ledstriprgb.data.model.LEDEffectItem
import quevedo.soares.leandro.ledstriprgb.generic.GenericViewModel
import quevedo.soares.leandro.ledstriprgb.network.repository.LEDRepository

class HomeFragmentViewModel(private val repository: LEDRepository) : GenericViewModel() {

	val animationList = listOf(
		LEDEffectItem(LEDEffect.RainbowCycle, R.drawable.shape_rounded_gradient_1),
		LEDEffectItem(LEDEffect.Rainbow, R.drawable.shape_rounded_gradient_2),
		LEDEffectItem(LEDEffect.SolidColor, R.drawable.shape_rounded_gradient_3),
		LEDEffectItem(LEDEffect.KnightRider, R.drawable.shape_rounded_gradient_4),
		LEDEffectItem(LEDEffect.Sparkle, R.drawable.shape_rounded_gradient_5),
		LEDEffectItem(LEDEffect.Pulse, R.drawable.shape_rounded_gradient_6),
		LEDEffectItem(LEDEffect.Fire, R.drawable.shape_rounded_gradient_7),
	)

	var state = MutableLiveData<ViewState>()

	/***
	 * Emits the [ViewState.Error] on [state]
	 ***/
	override fun dispatchError(exception: Exception) = state.postValue(ViewState.Error(exception))

	fun setAnimation(value: Int) = request {
		wrap { repository.setAnimation(value) }
	}

	fun setBrightness(value: Int) = request {
		wrap { repository.setBrightness(value) }?.let {
			state.postValue(ViewState.FetchBrightness(value))// Notify state
		}
	}

	fun getBrightness() = request {
		wrap { repository.getBrightness() }?.let {
			state.postValue(ViewState.FetchBrightness(it.toInt()))// Notify state
		}
	}

	fun setSpeed(value: Int) = request {
		wrap { repository.setSpeed(value) }
	}

	fun getSpeed() = request {
		wrap { repository.getSpeed() }?.let {
			state.postValue(ViewState.FetchSpeed(it.toFloat()))
		}
	}

	fun setColor(value: String) = request {
		wrap { repository.setColor(value) }
	}

	sealed class ViewState {

		class Error(val exception: Exception) : ViewState()

		class FetchBrightness(val value: Int) : ViewState()

		class FetchSpeed(val value: Float) : ViewState()

	}

	sealed class ActionState

}