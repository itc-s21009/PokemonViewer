package jp.ac.it_college.std.s21009.pokemonviewer.service

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonInfo(
    val sprites: Sprites,
    val types: List<Type>
)

@JsonClass(generateAdapter = true)
data class Sprites(
    val other: Other
)

@JsonClass(generateAdapter = true)
data class Other(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtwork
)

@JsonClass(generateAdapter = true)
data class OfficialArtwork(
    @Json(name = "front_default") val frontDefault: String
)

@JsonClass(generateAdapter = true)
data class TypeInfo(
    val names: List<Name>
)

@JsonClass(generateAdapter = true)
data class Name(
    val name: String,
    val language: NamedAPIResource
)

@JsonClass(generateAdapter = true)
data class Type(
    val slot: Int,
    @Json(name = "type") val resource: NamedAPIResource
)

@JsonClass(generateAdapter = true)
data class NamedAPIResource(
    val name: String,
    val url: String
)