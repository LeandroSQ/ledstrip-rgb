package quevedo.soares.leandro.ledstriprgb.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.abs

/***
 * Mimics the functionality of DispatchObject on iOS
 * [https://developer.apple.com/documentation/dispatch/dispatchgroup]
 *
 * Useful for handling concurrency
 *
 * Every job to keep track of needs to call [GroupDispatcher.enter]
 * At it's conclusion it's required to call [GroupDispatcher.exit]
 *
 * When all the jobs conclude both the [GroupDispatcher.onResetListener] and [GroupDispatcher.isFulfilled] will be invoked
 ***/
class GroupDispatcher(private var onResetListener: (() -> Unit)? = null) {

	// Internal counter
	private var stack = 0

	// Live data to keep track whenever the stack reaches zero
	private val _isFulfilled: MutableLiveData<Boolean> = MutableLiveData(true)
	val isFulfilled: LiveData<Boolean> get() = _isFulfilled

	fun enter(amount: Int = 1) {
		this.stack += abs(amount)

		// Update logic
		this.notifyChange()
	}

	fun exit(amount: Int = 1) {
		// Ignore 'exit' calls before 'enter'
		if (this.stack <= 0) return

		this.stack -= abs(amount)

		// Update logic
		this.notifyChange()
	}

	fun reset() {
		// Set the stack to zero
		this.stack = 0
		// Update live data and invoke the listener
		this.notifyChange()
	}

	private fun notifyChange() {
		// Check if the stack was set to zero
		val fulfilled = this.stack == 0
		// Update the live data
		this._isFulfilled.postValue(fulfilled)
		// If stack is now zero, invoke the listener
		if (fulfilled) this.onResetListener?.invoke()
	}

}