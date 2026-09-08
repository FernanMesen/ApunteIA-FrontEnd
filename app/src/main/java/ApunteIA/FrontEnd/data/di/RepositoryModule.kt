package ApunteIA.FrontEnd.data.di

import ApunteIA.FrontEnd.data.datasource.*
import ApunteIA.FrontEnd.data.repository.*
import ApunteIA.FrontEnd.domain.repository.*
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
    abstract fun bindMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(quizRepositoryImpl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(topicRepositoryImpl: TopicRepositoryImpl): TopicRepository

    @Binds
    @Singleton
    abstract fun bindMainDataSource(mainDataSourceImpl: MainDataSourceImpl): MainDataSource

    @Binds
    @Singleton
    abstract fun bindNoteDataSource(noteDataSourceImpl: NoteDataSourceImpl): NoteDataSource

    @Binds
    @Singleton
    abstract fun bindQuizDataSource(quizDataSourceImpl: QuizDataSourceImpl): QuizDataSource

    @Binds
    @Singleton
    abstract fun bindTopicDataSource(topicDataSourceImpl: TopicDataSourceImpl): TopicDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource
}