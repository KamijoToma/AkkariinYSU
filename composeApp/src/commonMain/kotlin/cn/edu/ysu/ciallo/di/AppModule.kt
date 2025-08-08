package cn.edu.ysu.ciallo.di

import cn.edu.ysu.ciallo.cardbalance.CardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository
import cn.edu.ysu.ciallo.gpa.GpaRepository
import cn.edu.ysu.ciallo.gpa.GpaViewModel
import cn.edu.ysu.ciallo.gpa.MockGpaRepository
import cn.edu.ysu.ciallo.gpa.RemoteGpaRepository
import cn.edu.ysu.ciallo.home.*
import cn.edu.ysu.ciallo.studentinfo.MockStudentInfoRepository
import cn.edu.ysu.ciallo.studentinfo.RemoteStudentInfoRepository
import cn.edu.ysu.ciallo.studentinfo.StudentInfoRepository
import cn.edu.ysu.ciallo.studentinfo.StudentInfoViewModel
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
    singleOf(::RemoteStudentInfoRepository) { bind<StudentInfoRepository>() }
    singleOf(::RemoteGpaRepository) { bind<GpaRepository>() }

    // ViewModels
    single { HomeViewModel(get()) }
    single { CardBalanceViewModel(get()) }
    factoryOf(::LoginViewModel)
    single { StudentInfoViewModel(get()) }
    single { GpaViewModel(get()) }
}

val previewModule = module {
    // Apis

    // Repositories
    singleOf(::FakeHomeRepository) { bind<HomeRepository>() }
    singleOf(::MockCardBalanceRepository) { bind<CardBalanceRepository>() }
    singleOf(::FakeLoginRepository) { bind<LoginRepository>() }
    singleOf(::MockStudentInfoRepository) { bind<StudentInfoRepository>() }
    singleOf(::MockGpaRepository) { bind<GpaRepository>() }

    // ViewModels
    single { HomeViewModel(get()) }
    single { CardBalanceViewModel(get()) }
    factoryOf(::LoginViewModel)
    single { StudentInfoViewModel(get()) }
    single { GpaViewModel(get()) }
}
