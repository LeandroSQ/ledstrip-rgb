package quevedo.soares.leandro.ledstriprgb.di

import androidx.work.WorkerParameters
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import quevedo.soares.leandro.ledstriprgb.worker.PowerToggleWorker

internal val workerModule = module {
	worker { (params: WorkerParameters) -> PowerToggleWorker(get(), androidContext(), params) }
}
