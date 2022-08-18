package jp.ac.it_college.std.s21009.pokemonviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.ac.it_college.std.s21009.pokemonviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val pokemonList = mapOf<String, Int>(
        "フシギダネ" to 1,
        "ヒトカゲ" to 4,
        "ゼニガメ" to 7,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}