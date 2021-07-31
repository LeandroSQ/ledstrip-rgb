package quevedo.soares.leandro.ledstriprgb.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import quevedo.soares.leandro.ledstriprgb.view.home.HomeFragmentViewModel

internal val viewModelModule = module {
	viewModel { HomeFragmentViewModel(get()) }
}