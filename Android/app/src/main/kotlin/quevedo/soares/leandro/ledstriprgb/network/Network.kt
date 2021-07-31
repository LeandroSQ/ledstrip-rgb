package quevedo.soares.leandro.ledstriprgb.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Network {

	private val loggingInterceptor by lazy {
		HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}
	}

	private val okHttpClient by lazy {
		OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.build()
	}

	private val moshi by lazy {
		Moshi.Builder()
			.build()
	}

	private val moshiConverter by lazy {
		MoshiConverterFactory.create(moshi)
	}

	fun getRetrofitInstance(host: String): Retrofit = Retrofit.Builder()
		.baseUrl(host)
		.client(okHttpClient)
		.addConverterFactory(moshiConverter)
		.build()

	fun <T> getService(retrofit: Retrofit, serviceClass: Class<T>): T = retrofit.create(serviceClass)

}