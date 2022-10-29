package ru.mobileprism.autoredemption.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.compose.get
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.mobileprism.autoredemption.utils.Constants
import java.util.concurrent.TimeUnit

val networkModule = module {

    //Create HttpLoggingInterceptor
    single<HttpLoggingInterceptor> { createLoggingInterceptor() }

    //Create OkHttpClient
    single<OkHttpClient> { createOkHttpClient(get()) }

    single<ApolloClient> { createApolloClient(get()) }



}

fun createApolloClient(okHttpClient: OkHttpClient) = ApolloClient.Builder()
        .okHttpClient(okHttpClient)
        .serverUrl(Constants.apiUrl)
        .build()


fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().setLevel(
        if (Constants.isDebug) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    )
}

/**
 * Create a OkHttpClient which is used to send HTTP requests and read their responses.
 * @loggingInterceptor logging interceptor
 */
private fun createOkHttpClient(
    loggingInterceptor: HttpLoggingInterceptor,
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
}