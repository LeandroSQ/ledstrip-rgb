package quevedo.soares.leandro.ledstriprgb.generic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.EMPTY_RESPONSE
import quevedo.soares.leandro.ledstriprgb.extension.exception
import quevedo.soares.leandro.ledstriprgb.helper.GroupDispatcher
import retrofit2.Response

typealias ResponseCallback<T> = (suspend () -> Response<T>)

abstract class GenericViewModel : ViewModel() {

	private val _loaderGroupDispatcher = GroupDispatcher()
	val isAnyRequestAlive get() = this._loaderGroupDispatcher.isFulfilled

	abstract fun dispatchError(exception: Exception)

	private suspend fun <T> call(apiCall: ResponseCallback<T>): Response<T> {
		// Notify that an api call is in progress
		_loaderGroupDispatcher.enter()

		try {
			// Invokes the API call
			return apiCall.invoke()
		} catch (e: Exception) {
			e.printStackTrace()

			dispatchError(e)
		} finally {
			// Notify that an api has finished
			_loaderGroupDispatcher.exit()
		}

		// Unknown response error
		return Response.error(0, EMPTY_RESPONSE)
	}

	/***
	 * Wrapper for [GenericViewModel.call]
	 * Automatically handles errors and dispatches them to their view model
	 ***/
	protected suspend fun <T> wrap(apiCall: ResponseCallback<T>): T? {
		val response = call(apiCall)

		if (response.isSuccessful) {
			return response.body()
		} else {
			dispatchError(response.exception)
		}

		return null
	}

	protected fun request(callback: suspend () -> Unit) {
		GlobalScope.launch(Dispatchers.IO) {
			try {
				callback.invoke()
			} catch (e: Exception) {
				e.printStackTrace()

				dispatchError(e)
			}
		}
	}

}