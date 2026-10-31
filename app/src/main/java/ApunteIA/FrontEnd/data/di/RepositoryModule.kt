package ApunteIA.FrontEnd.data.di

import ApunteIA.FrontEnd.data.datasource.*
import ApunteIA.FrontEnd.data.ocr.MlKitTextRecognizer
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

    // ---------- Repositorios ----------

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMainRepository(impl: MainRepositoryImpl): MainRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(impl: TopicRepositoryImpl): TopicRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(impl: CourseRepositoryImpl): CourseRepository

    @Binds
    @Singleton
    abstract fun bindSessionStore(impl: ApunteIA.FrontEnd.data.session.SessionManager): ApunteIA.FrontEnd.data.session.SessionStore

    @Binds
    @Singleton
    abstract fun bindTextRecognizer(impl: MlKitTextRecognizer): TextRecognizer

    // ---------- Fuentes de datos ----------

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(impl: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindNoteDataSource(impl: NoteDataSourceImpl): NoteDataSource

    @Binds
    @Singleton
    abstract fun bindQuizDataSource(impl: QuizDataSourceImpl): QuizDataSource

    @Binds
    @Singleton
    abstract fun bindCourseDataSource(impl: CourseDataSourceImpl): CourseDataSource
}