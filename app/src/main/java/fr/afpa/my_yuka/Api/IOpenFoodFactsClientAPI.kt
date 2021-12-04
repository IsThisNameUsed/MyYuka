package fr.afpa.my_yuka.Api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class IOpenFoodFactsClientAPI {

    private val retrofit: Retrofit = if(android.os.Build.VERSION.SDK_INT >= 26) {
        Log.d("OKHTTTP", "safe Okhttp")
        Retrofit.Builder().baseUrl(IOpenFoodFactsAPI.ENDPOINT).addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    else {
        Log.d("OKHTTP3", "Unsafe Okhttp")
        Retrofit.Builder().baseUrl(IOpenFoodFactsAPI.ENDPOINT).client(UnsafeOkHttpClient.unsafeOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create()).build()
    }

    val service = retrofit.create(IOpenFoodFactsAPI::class.java)
}