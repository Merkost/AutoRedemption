package ru.mobileprism.autoredemption.di

import android.media.session.MediaSession.Token
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.compose.get
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.mobileprism.autoredemption.utils.Constants
import ru.mobileprism.autoredemption.utils.TokensInterceptor
import java.time.Duration
import java.util.concurrent.TimeUnit

val networkModule = module {

    //Create HttpLoggingInterceptor
    single<HttpLoggingInterceptor> { createLoggingInterceptor() }
    single<TokensInterceptor> { TokensInterceptor(userDatastore = get()) }

    //Create OkHttpClient
    single<OkHttpClient> {
        createOkHttpClient(
            loggingInterceptor = get(),
            tokensInterceptor = get()
        )
    }

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
    tokensInterceptor: TokensInterceptor,
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(tokensInterceptor)
        .connectTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
}