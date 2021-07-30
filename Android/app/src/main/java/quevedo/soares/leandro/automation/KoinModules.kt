package quevedo.soares.leandro.automation

import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.dsl.module
import quevedo.soares.leandro.automation.network.Network
import quevedo.soares.leandro.automation.network.datasource.LEDRemoteDataSource
import quevedo.soares.leandro.automation.network.repository.LEDRepository
import quevedo.soares.leandro.automation.network.service.LEDService
import quevedo.soares.leandro.automation.service.PowerToggleService
import quevedo.soares.leandro.automation.view.led.LEDFragmentViewModel

private val serviceModule = module {
	single { Network.getRetrofitInstance("http://192.168.25.78/") }
	single { Network.getService(get(), LEDService::class.java) }
}

private val dataSourceModule = module {
	single { LEDRemoteDataSource(get()) }
}

private val repositoryModule = module {
	single { LEDRepository(get()) }
}

private val viewModelModule = module {
	viewModel { LEDFragmentViewModel(get()) }
}

private val workerModule = module {
	worker { (params: WorkerParameters) -> PowerToggleService(get(), androidContext(), params) }
}

@KoinExperimentalAPI
fun setupKoin(application: Application) {
	startKoin {
		androidContext(application)

		workManagerFactory()

		modules(listOf(serviceModule, dataSourceModule, repositoryModule, viewModelModule, workerModule))
	}
}