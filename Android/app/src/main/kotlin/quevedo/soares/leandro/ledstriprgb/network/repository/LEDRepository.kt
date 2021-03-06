package quevedo.soares.leandro.ledstriprgb.network.repository

import quevedo.soares.leandro.ledstriprgb.network.datasource.LEDRemoteDataSource


class LEDRepository (private val dataSource: LEDRemoteDataSource) {

	suspend fun setBrightness(value: Int) = dataSource.setBrightness(value)

	suspend fun togglePower() = dataSource.togglePower()

	suspend fun getBrightness() = dataSource.getBrightness()

	suspend fun setSpeed(value: Int) = dataSource.setSpeed(value)

	suspend fun getSpeed() = dataSource.getSpeed()

	suspend fun setAnimation(value: Int) = dataSource.setAnimation(value)

	suspend fun setColor(value: String) = dataSource.setColor(value)

}