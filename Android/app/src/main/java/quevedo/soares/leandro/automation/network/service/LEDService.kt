package quevedo.soares.leandro.automation.network.service

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LEDService {

	@POST("brightness")
	@FormUrlEncoded
	suspend fun setBrightness(@Field("value") value: String): Response<Unit>

	@GET("brightness")
	suspend fun getBrightness(): Response<String>

	@POST("speed")
	@FormUrlEncoded
	suspend fun setSpeed(@Field("value") value: String): Response<Unit>

	@GET("speed")
	suspend fun getSpeed(): Response<String>

	@POST("animation")
	@FormUrlEncoded
	suspend fun setAnimation(@Field("value") value: String): Response<Unit>

	@POST("color")
	@FormUrlEncoded
	suspend fun setColor(@Field("value") value: String): Response<Unit>

}