package quevedo.soares.leandro.ledstriprgb.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin
import quevedo.soares.leandro.ledstriprgb.Application

@KoinExperimentalAPI
fun setupKoin(application: Application) {
	startKoin {
		androidContext(application)

		workManagerFactory()

		modules(listOf(serviceModule, dataSourceModule, repositoryModule, viewModelModule, workerModule))
	}
}