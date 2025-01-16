package com.example.pokeapiMejorado.network



import com.example.pokeapiMejorado.model.GenerationResponse
import com.example.pokeapiMejorado.model.PokemonResponse

import retrofit2.http.GET
import retrofit2.http.Path



interface PokeApiService {

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonResponse

    @GET("generation/{id}")
    suspend fun getPokemonByGeneration(@Path("id") id: Int): GenerationResponse
}






