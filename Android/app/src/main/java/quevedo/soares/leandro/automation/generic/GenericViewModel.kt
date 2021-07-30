package quevedo.soares.leandro.automation.generic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import quevedo.soares.leandro.automation.util.GroupDispatcher
import retrofit2.Response

open class GenericViewModel : ViewModel() {

	private val _loaderGroupDispatcher = GroupDispatcher()
	val isLoading get() = this._loaderGroupDispatcher.isFulfilled

	val error = MutableLiveData<Exception>()

	fun <T> getMessage(response: Response<T>): String {
		try {
			val body = response.raw().body?.string()
			if (body != null) return body

			val errorBody = response.errorBody()?.string()
			if (errorBody != null) return errorBody
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return "Unknown error!"
	}

	fun <T> getException(response: Response<T>): Exception {
		return Exception(this.getMessage(response))
	}

	protected fun <T> prepare(callback: (suspend () -> Response<T>)) {
		_loaderGroupDispatcher.enter()

		GlobalScope.launch(Dispatchers.IO) {
			try {
				val response = callback.invoke()

				if (!response.isSuccessful) error.postValue(getException(response))
			} catch (e: Exception) {
				e.printStackTrace()
				error.postValue(e)
			} finally {
				_loaderGroupDispatcher.exit()
			}

		}
	}

}