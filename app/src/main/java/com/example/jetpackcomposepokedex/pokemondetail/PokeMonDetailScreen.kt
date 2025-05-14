package com.example.jetpackcomposepokedex.pokemondetail
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposepokedex.data.remote.responses.Type
import com.example.jetpackcomposepokedex.util.parseTypeToColor
import java.util.Locale
import com.example.jetpackcomposepokedex.R
import com.example.jetpackcomposepokedex.util.parseStatToAbbr
import com.example.jetpackcomposepokedex.util.parseStatToColor

@Composable
fun PokeMonDetailScreen(
    dominantColor: Color?,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailVM = hiltViewModel()
) {

    val pokeInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor!!)
            .padding(bottom = 16.dp)
    )
    {
        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                    .fillMaxHeight(0.2f)
        )
        PokemonDetailWrapper(
            pokemonInfo = pokeInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
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

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        )
        {
            if (pokeInfo is Resource.Success) {
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
) {
    Box(
        contentAlignment = Alignment.TopStart,
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

    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back",
            tint = Color.White, modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController.popBackStack()
                })

    }
}

@Composable
fun PokemonDetailWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
//            Box(modifier = modifier.background(Color.Black)) {
                PokemonDetailInfoSection(
                    pokemonInfo = pokemonInfo.data!!,
                    modifier = modifier.offset(y = (-20).dp)
                )
//            }
        }

        is Resource.Error<*> -> {
            Text(text = pokemonInfo.message.toString(), color = Color.Red, modifier = modifier)
        }

        is Resource.Loading<*> -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailInfoSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    )
    {
        Text(
            text = "#${pokemonInfo.id} ${pokemonInfo.name.capitalize(Locale.ROOT)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(
            pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height
        )
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }

}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ) {

        for (type in types) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(parseTypeToColor(type))
            ) {
                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 18.sp
                )
            }
        }
    }

}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {

    val pokemonWeightInKg = remember {
        pokemonWeight * 100f / 1000f
    }
    val pokemonHeightInMeters = remember {
        pokemonHeight * 100f / 1000f
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        PokemonDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "KG",
            dataIcon = painterResource(id = R.drawable.baseline_monitor_weight_24),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(Color.LightGray)
        )
        PokemonDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "M",
            dataIcon = painterResource(id = R.drawable.baseline_monitor_weight_24),
            modifier = Modifier.weight(1f)
        )

    }
}

@Composable
fun PokemonDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            painter = dataIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    stateColor: Color,
    height: Dp = 28.dp,
    animationDuration: Int = 1000,
    animDelay: Int = 10
){
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currentPercent = animateFloatAsState(
        targetValue = if (animationPlayed){
            statValue / statMaxValue.toFloat()
        } else 0f,
        animationSpec = tween(animationDuration,animDelay)
    )

    LaunchedEffect(true) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()){
                    Color(0xFF505050)
                }else{
                    Color.LightGray
                }
            )
    ){
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight( )
                .fillMaxWidth(currentPercent.value)
                .clip(CircleShape)
                .background(stateColor)
                .padding(horizontal = 8.dp)
        ){

            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (currentPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )

        }

    }


}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100,

){
    val maxBaseState = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }


    Column (modifier = Modifier.fillMaxWidth()){
        Text(text = "Base Stats: ", fontSize = 20.sp, color = MaterialTheme.colorScheme.surface)
        Spacer(modifier = Modifier.height(4.dp))

        for (i in pokemonInfo.stats.indices){
            val stat = pokemonInfo.stats[i]
            PokemonStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseState,
                stateColor = parseStatToColor(stat),
                animDelay =  i * animDelayPerItem,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}