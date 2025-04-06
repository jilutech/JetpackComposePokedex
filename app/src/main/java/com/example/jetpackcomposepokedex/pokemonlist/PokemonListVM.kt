package com.example.jetpackcomposepokedex.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
 import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.jetpackcomposepokedex.data.model.PokeListEntry
 import com.example.jetpackcomposepokedex.repo.PokeMonRepo
import com.example.jetpackcomposepokedex.util.Constant.PAGE_SIZE
import com.example.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.internal.filterList
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class PokemonListVM @Inject constructor(
    private val pokemonRepository: PokeMonRepo
) : ViewModel() {

    private var currentPage = 0

    var pokemonList = mutableStateOf<List<PokeListEntry>>(listOf())

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokeListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)





    init {
        loadPokeMOnPaginated()
    }

    fun pokemonSearchList(query : String){

        var listToSearch =  if (isSearchStarting){
            pokemonList.value
        }else{
            cachedPokemonList
        }

        viewModelScope.launch (Dispatchers.Default){
            if (query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            var result = listToSearch.filter { pokemonListEntry ->
                pokemonListEntry.pokemonName.contains(query.trim(),ignoreCase = true) ||
                        pokemonListEntry.number.toString() == query.trim()
            }
            if (isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = result
            isSearching.value = true
        }
    }
    fun loadPokeMOnPaginated(){

        viewModelScope.launch {
            isLoading.value = true
            val result = pokemonRepository.getPokeMonList(PAGE_SIZE,currentPage * PAGE_SIZE)
            when(result){
                is Resource.Success -> {
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count

                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokeListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    currentPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit){
        val bitmap  = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)
        Palette.from(bitmap).generate{
            it?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

}