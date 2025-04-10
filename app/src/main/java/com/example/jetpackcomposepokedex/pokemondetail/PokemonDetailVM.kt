package com.example.jetpackcomposepokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposepokedex.repo.PokeMonRepo
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class PokemonDetailVM @Inject constructor(
    private val repo: PokeMonRepo
) : ViewModel(){
    suspend fun getPokemonInfo(pokemonName : String) = repo.getPokemonInfo(pokemonName)
}