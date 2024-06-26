package cu.suitetecsa.sdkandroid.di

import android.content.Context
import cu.suitetecsa.sdkandroid.data.source.PreferenceDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.suitetecsa.sdk.android.ContactsCollector
import io.github.suitetecsa.sdk.android.SimCardCollector
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataSource(@ApplicationContext context: Context): PreferenceDataSource =
        PreferenceDataSource(context = context)

    @Provides
    @Singleton
    fun provideSimCardApi(@ApplicationContext context: Context): SimCardCollector =
        SimCardCollector.Builder().build(context)

    @Provides
    @Singleton
    fun provideContactsCollector(@ApplicationContext context: Context): ContactsCollector =
        ContactsCollector.Builder().build(context)
}
