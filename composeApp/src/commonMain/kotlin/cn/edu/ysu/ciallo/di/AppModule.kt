package cn.edu.ysu.ciallo.di

import cn.edu.ysu.ciallo.cardbalance.CardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository
import cn.edu.ysu.ciallo.home.*
import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Apis
    single<YsuEhallApi> { YsuEhallApiFactory.getInstance() }

    // Repositories
    singleOf(::RemoteHomeRepository) { bind<HomeRepository>() }
    singleOf(::RemoteCardBalanceRepository) { bind<CardBalanceRepository>() }
    singleOf(::RemoteLoginRepository) { bind<LoginRepository>() }

    // ViewModels
    single { HomeViewModel(get()) }
    single { CardBalanceViewModel(get()) }
    factoryOf(::LoginViewModel)
}

val previewModule = module {
    // Apis

    // Repositories
    singleOf(::FakeHomeRepository) { bind<HomeRepository>() }
    singleOf(::MockCardBalanceRepository) { bind<CardBalanceRepository>() }
    singleOf(::FakeLoginRepository) { bind<LoginRepository>() }

    // ViewModels
    single { HomeViewModel(get()) }
    single { CardBalanceViewModel(get()) }
    factoryOf(::LoginViewModel)
}
