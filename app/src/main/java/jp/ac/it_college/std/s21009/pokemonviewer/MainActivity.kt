package jp.ac.it_college.std.s21009.pokemonviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import jp.ac.it_college.std.s21009.pokemonviewer.databinding.ActivityMainBinding
import jp.ac.it_college.std.s21009.pokemonviewer.service.PokemonInfo
import jp.ac.it_college.std.s21009.pokemonviewer.service.PokemonService
import jp.ac.it_college.std.s21009.pokemonviewer.service.SpeciesInfo
import jp.ac.it_college.std.s21009.pokemonviewer.service.TypeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

private const val BASE_URL = "https://pokeapi.co/"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        addConverterFactory(MoshiConverterFactory.create(moshi))
    }.build()
    private val service: PokemonService = retrofit.create(PokemonService::class.java)
    private val pokemonList = mapOf(
        "フシギダネ" to 1,
        "フシギソウ" to 2,
        "フシギバナ" to 3,
        "ヒトカゲ" to 4,
        "リザード" to 5,
        "リザードン" to 6,
        "ゼニガメ" to 7,
        "カメール" to 8,
        "カメックス" to 9,
        "ピカチュウ" to 25,
        "イーブイ" to 133,
        "ルカリオ" to 448,
        "カイリュー" to 149,
        "ゲッコウガ" to 658,
        "ニンフィア" to 700,
        "ミュウ" to 151,
        "アブソル" to 359,
        "レックウザ" to 384,
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
        binding.spPokemon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.etId.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.btDisplay.setOnClickListener {
            val text = binding.etId.text.toString()
            val id = if (text.isEmpty()) pokemonList[binding.spPokemon.selectedItem] else text.toInt()
            showPokemonInfo(id ?: throw IllegalArgumentException("存在しないポケモンが選ばれました"))
        }
    }

    @UiThread
    private fun showPokemonInfo(id: Int) {
        lifecycleScope.launch {
            try {
                val info = getPokemonInfo(id)!!
                Picasso.get().load(info.sprites.other.officialArtwork.frontDefault).into(binding.imgPokemon)
                val typeNameList = info.types.map {
                    val typeId = getNumberAtEndOfURL(it.type.url)
                    getTypeInfo(typeId)!!.names.filter { n -> n.language.name == "ja-Hrkt" }[0].name
                }
                val speciesId = getNumberAtEndOfURL(info.species.url)
                val species = getSpeciesInfo(speciesId)!!
                val japaneseText = species.flavorTexts.filter { text -> text.language.name == "ja" }[0].flavorText
                val genus = species.genera.filter { g -> g.language.name == "ja-Hrkt"}[0].genus
                val name = species.names.filter { n -> n.language.name == "ja-Hrkt"}[0].name
                binding.tvType.text = getString(R.string.type,
                    typeNameList.joinToString("\n") { "・${it}" })
                binding.tvWeight.text = getString(R.string.weight, info.weight)
                binding.tvGenus.text = getString(R.string.genus, genus)
                binding.tvFlavorText.text = japaneseText
                binding.tvPokemonName.text = getString(R.string.pokemon_name, name)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, R.string.not_found , Toast.LENGTH_LONG).show()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @WorkerThread
    private suspend fun getPokemonInfo(id: Int): PokemonInfo? {
        return withContext(Dispatchers.IO) {
            service.fetchPokemon(id).execute().body()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @WorkerThread
    private suspend fun getTypeInfo(id: Int): TypeInfo? {
        return withContext(Dispatchers.IO) {
            service.fetchType(id).execute().body()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @WorkerThread
    private suspend fun getSpeciesInfo(id: Int): SpeciesInfo? {
        return withContext(Dispatchers.IO) {
            service.fetchSpecies(id).execute().body()
        }
    }

    private fun getNumberAtEndOfURL(url: String): Int {
        val split= url.split("/")
        return split[split.size - 2].toInt()
    }

}