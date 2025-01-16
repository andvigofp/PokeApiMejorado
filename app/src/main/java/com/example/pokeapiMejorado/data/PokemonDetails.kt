package com.example.pokeapiMejorado.data

import com.example.pokeapiMejorado.model.Type

data class PokemonDetails(
    val id: Int, val name: String,
    val types: List<PokemonTypeSlot>
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: Type
)
