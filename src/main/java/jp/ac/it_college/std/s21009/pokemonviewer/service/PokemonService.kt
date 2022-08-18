package jp.ac.it_college.std.s21009.pokemonviewer.service

import retrofit2.Call
import retrofit2.http.GET

abstract class PokemonService {
    @GET("api/v2/pokemon/")
    abstract fun fetchWeather(
    ): Call<PokemonInfo>
}