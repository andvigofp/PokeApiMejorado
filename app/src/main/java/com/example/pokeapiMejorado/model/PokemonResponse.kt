package com.example.pokeapiMejorado.model


data class PokemonResponse(
    val name: String,
    val types: List<TypeInfo>,
    val sprites: Sprites // Agregamos el campo para los sprites
)

data class TypeInfo(
    val type: Type
)

data class Type(
    val name: String
)

data class GenerationResponse(
    val id: Int,
    val name: String,
    val pokemon_species: List<PokemonSpecies>
)

data class PokemonSpecies(
    val name: String,
    val url: String // Podría ser útil si necesitas detalles adicionales del Pokémon
)

// Clase para los sprites
data class Sprites(
    val front_default: String?  // URL de la imagen del sprite
)
