package quevedo.soares.leandro.automation.network.datasource

import quevedo.soares.leandro.automation.network.service.LEDService


class LEDRemoteDataSource(private val service: LEDService) {

	suspend fun setBrightness(value: Int) = service.setBrightness(value.toString())

	suspend fun getBrightness() = service.getBrightness()

	suspend fun setSpeed(value: Int) = service.setSpeed(value.toString())

	suspend fun getSpeed() = service.getSpeed()

	suspend fun setAnimation(value: Int) = service.setAnimation(value.toString())

	suspend fun setColor(value: String) = service.setColor(value)

}