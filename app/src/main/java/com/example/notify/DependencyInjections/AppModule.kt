package com.example.notify.DependencyInjections

import com.example.notify.Services.AuthService.Authentication
import com.example.notify.Services.AuthService.AuthenticationImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) //make sure object lives as long as our application does
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesAuthImpl(firebaseAuth: FirebaseAuth): Authentication {
        return AuthenticationImpl(firebaseAuth)
    }
}