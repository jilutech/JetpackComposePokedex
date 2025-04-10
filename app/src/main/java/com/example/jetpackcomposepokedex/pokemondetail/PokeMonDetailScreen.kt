package com.example.jetpackcomposepokedex.pokemondetail
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.util.Resource

import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment

@Composable
fun PokeMonDetailScreen (
    dominantColor: Color?,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailVM = hiltViewModel()
){

    val pokeInfo = produceState<Resource<Pokemon>>( initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }.value
    Box(modifier = Modifier
        .fillMaxSize()
        .background(dominantColor!!)
        .padding(bottom = 16.dp))
    {
    PokemonDetailTopSection(
        navController = navController,
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight(0.2f)
    )
    PokemonDetailWrapper(
        pokemonInfo = pokeInfo,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topPadding + pokemonImageSize / 2f, start = 16.dp, end = 16.dp, bottom = 16.dp
            )
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        loadingModifier = Modifier
            .size(100.dp)
            .align(Alignment.TopCenter)
            .padding(top = topPadding + pokemonImageSize / 2f)

    )

        Box(contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
            ){
            if (pokeInfo is Resource.Success){
                pokeInfo.data?.sprites?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.front_default)
                            .crossfade(true)
                            .build(),
                        contentDescription = pokeInfo.data.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
)
{
    Box(contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
            .padding(16.dp)

    ){
       Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back",
            tint = Color.White, modifier = Modifier.size(30.dp).clickable{
                navController.popBackStack()
            })

    }
}

@Composable
fun PokemonDetailWrapper(
    pokemonInfo :  Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier : Modifier = Modifier
){
    when(pokemonInfo){
        is Resource.Success -> {
            Box(modifier = modifier.background(Color.LightGray)) {
            }
        }
        is Resource.Error<*> -> {
            Text(text = pokemonInfo.message.toString(), color = Color.Red, modifier = modifier )
        }
        is Resource.Loading<*> -> {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = loadingModifier)
        }
    }
}

