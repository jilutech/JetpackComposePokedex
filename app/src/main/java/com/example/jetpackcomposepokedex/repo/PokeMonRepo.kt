package com.example.jetpackcomposepokedex.repo

import com.example.jetpackcomposepokedex.data.remote.PokeApi
import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.data.remote.responses.PokemonList
import com.example.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokeMonRepo @Inject constructor(
    private val api: PokeApi
){
    suspend fun getPokeMonList(limit:Int,offset:Int):Resource<PokemonList>{
        val response = try {
            api.getPokeMonList(limit,offset)
        }catch (e: Exception){
          return Resource.Error("Error")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(name: String): Resource<Pokemon>{
        val  response = try {
            api.getPokemonInfo(name)

        }catch (e: Exception){
            return Resource.Error("Error")
        }
        return Resource.Success(response)
    }
}