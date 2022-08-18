package jp.ac.it_college.std.s21009.pokemonviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toolbar
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import jp.ac.it_college.std.s21009.pokemonviewer.databinding.ActivityMainBinding
import jp.ac.it_college.std.s21009.pokemonviewer.service.PokemonInfo
import jp.ac.it_college.std.s21009.pokemonviewer.service.PokemonService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

private const val BASE_URL = "https://pokeapi.co/"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val pokemonList = mapOf(
        "フシギダネ" to 1,
        "ヒトカゲ" to 4,
        "ゼニガメ" to 7,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spPokemon.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            pokemonList.keys.toTypedArray()
        )
        binding.btDisplay.setOnClickListener {
            val id = pokemonList[binding.spPokemon.selectedItem]
            showPokemonInfo(id?:throw IllegalArgumentException("存在しないポケモンが選ばれました"))
        }
    }

    @UiThread
    private fun showPokemonInfo(id: Int) {
        lifecycleScope.launch {
            val info = getPokemonInfo(id)
            Picasso.get().load(info.sprites.other.officialArtwork.frontDefault).into(binding.imgPokemon)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @WorkerThread
    private suspend fun getPokemonInfo(id: Int): PokemonInfo {
        return withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder().apply {
                baseUrl(BASE_URL)
                addConverterFactory(MoshiConverterFactory.create(moshi))
            }.build()
            val service: PokemonService = retrofit.create(PokemonService::class.java)
            service.fetchPokemon(id).execute().body() ?: throw IllegalStateException("ポケモンが取れませんでした")
        }
    }

}