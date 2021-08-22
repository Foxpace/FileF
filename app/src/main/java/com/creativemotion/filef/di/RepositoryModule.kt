package com.creativemotion.filef.di

import com.creativemotion.filef.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    /**
     * possibility to add database with history of searches
     */
    @Provides
    @ViewModelScoped
    fun generateMainRepository(): MainRepository {
//        val db: ArticleDao = DatabaseBuilder.getDatabase(context)
        return MainRepository()
    }

}