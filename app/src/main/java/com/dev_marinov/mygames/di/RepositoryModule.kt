package com.dev_marinov.mygames.di

import com.dev_marinov.mygames.data.GamesRepository
import com.dev_marinov.mygames.domain.IGameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(gamesRepository: GamesRepository)
    : IGameRepository

}

