package quevedo.soares.leandro.ledstriprgb.extension

import retrofit2.Response

/***
 * Formats the response text from response and error body into a string
 ***/
val <T> Response<T>.formattedMessage: String
	get() {
		try {
			val body = raw().body?.string()
			if (body != null) return body

			val errorBody = errorBody()?.string()
			if (errorBody != null) return errorBody
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return "Unknown error!"
	}

/***
 * Creates an exception with the message from [Response.formattedMessage]
 ***/
val <T> Response<T>.exception: Exception
	get() = Exception(this.formattedMessage)