package com.example.pokeapiMejorado.data

import com.example.pokeapiMejorado.network.PokeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val apiService: PokeApiService
}

class DefaultAppContainer : AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/") // Tu URL base de la API
        .addConverterFactory(GsonConverterFactory.create()) // Usando Gson para la conversión
        .build()

    override val apiService: PokeApiService by lazy {
        retrofit.create(PokeApiService::class.java)
    }
}