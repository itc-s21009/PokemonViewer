package jp.ac.it_college.std.s21009.pokemonviewer.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {
    @GET("api/v2/pokemon/{id}")
    fun fetchPokemon(
        @Path("id") id: Int
    ): Call<PokemonInfo>
}