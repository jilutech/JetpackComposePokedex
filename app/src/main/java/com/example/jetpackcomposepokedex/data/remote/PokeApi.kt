package com.example.jetpackcomposepokedex.data.remote

import androidx.compose.ui.geometry.Offset
import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.data.remote.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokeMonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
         @Path("name") name:String
    ): Pokemon



}