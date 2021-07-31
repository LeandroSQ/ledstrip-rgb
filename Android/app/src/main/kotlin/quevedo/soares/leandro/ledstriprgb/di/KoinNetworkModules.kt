package quevedo.soares.leandro.ledstriprgb.di

import org.koin.dsl.module
import quevedo.soares.leandro.ledstriprgb.BuildConfig
import quevedo.soares.leandro.ledstriprgb.network.Network
import quevedo.soares.leandro.ledstriprgb.network.datasource.LEDRemoteDataSource
import quevedo.soares.leandro.ledstriprgb.network.repository.LEDRepository
import quevedo.soares.leandro.ledstriprgb.network.service.LEDService

internal val serviceModule = module {
	single { Network.getRetrofitInstance(BuildConfig.SERVER_URL) }
	single { Network.getService(get(), LEDService::class.java) }
}

internal val dataSourceModule = module {
	single { LEDRemoteDataSource(get()) }
}

internal val repositoryModule = module {
	single { LEDRepository(get()) }
}