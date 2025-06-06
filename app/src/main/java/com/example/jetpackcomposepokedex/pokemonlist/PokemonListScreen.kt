package com.example.jetpackcomposepokedex.pokemonlist
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposepokedex.R
import com.example.jetpackcomposepokedex.data.model.PokeListEntry


@Composable
fun PokemonListScreen(
    navController: NavController,
    viewmodelpok: PokemonListVM = hiltViewModel()
) {

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("pokemon_list_screen")
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            searchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                viewmodelpok.pokemonSearchList(it)
             }
            Spacer(modifier = Modifier.height(20.dp))
            PokemonList(navController = navController)

        }
    }
}
@Composable
fun searchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }


    Box(modifier) {
        BasicTextField(value = text, onValueChange = {
            text = it
            onSearch(it)
        },
            maxLines = 1, singleLine = true, textStyle = TextStyle(Color.Black),
            modifier = Modifier.fillMaxWidth().shadow(5.dp, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged { focus ->
                    isHintDisplayed = !focus.isFocused && text.isNotEmpty()
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }

}

@Composable
fun PokemonList(
    navController: NavController,
    viewmodelpok: PokemonListVM = hiltViewModel()
){
    val pokemonList by remember {viewmodelpok.pokemonList}
    val endReached by remember {viewmodelpok.endReached}
    val loadError by remember {viewmodelpok.loadError}
    val isLoading by remember {viewmodelpok.isLoading}
    val isSearching by remember {viewmodelpok.isSearching}

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {

        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }

        items(itemCount) {
            if (it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewmodelpok.loadPokeMOnPaginated()
            }
            if (it == itemCount - 1){
                viewmodelpok.loadPokeMOnPaginated()
            }
            PokeRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewmodelpok.loadPokeMOnPaginated()
            }
        }
    }
}

@Composable
fun RetrySection(
    error : String,
    onRetry : () -> Unit
){
    Column {
        Text(text = error, fontSize = 18.sp, color = Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }

    }
}


@Composable
fun PokeEntry(
    entry : PokeListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewmodelpok: PokemonListVM = hiltViewModel()
){
    val defaultDominantColor = MaterialTheme.colorScheme.primary
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.ImageUrl)
                    .crossfade(true)
                    .listener(
                        onSuccess = { _, result ->
                            viewmodelpok.calcDominantColor(result.drawable) { color ->
                                dominantColor = color
                            }
                        }
                    )
                    .build(),
                contentDescription = entry.pokemonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = entry.pokemonName,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokeRow(
    rowIndex : Int,
    entries : List<PokeListEntry>,
    navController: NavController
) {
    Column {
        Row {
            PokeEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (entries.size >= rowIndex * 2 + 2) {
            PokeEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    @Composable
    fun RetrySection(
        error: String,
        onRetry: () -> Unit
    ) {
        Column {
            Text(text = error, fontSize = 18.sp, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onRetry() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Retry")
            }

        }
    }

}