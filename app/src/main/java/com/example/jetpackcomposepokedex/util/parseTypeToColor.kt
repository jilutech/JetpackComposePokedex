package com.example.jetpackcomposepokedex.util

import androidx.compose.ui.graphics.Color
import com.example.jetpackcomposepokedex.data.remote.responses.Stat
import com.example.jetpackcomposepokedex.data.remote.responses.Type
import com.example.jetpackcomposepokedex.ui.theme.AtkColor
import com.example.jetpackcomposepokedex.ui.theme.DefColor
import com.example.jetpackcomposepokedex.ui.theme.HPColor
import com.example.jetpackcomposepokedex.ui.theme.SpAtkColor
import com.example.jetpackcomposepokedex.ui.theme.SpDefColor
import com.example.jetpackcomposepokedex.ui.theme.SpdColor
import com.example.jetpackcomposepokedex.ui.theme.TypeBug
import com.example.jetpackcomposepokedex.ui.theme.TypeDark
import com.example.jetpackcomposepokedex.ui.theme.TypeDragon
import com.example.jetpackcomposepokedex.ui.theme.TypeElectric
import com.example.jetpackcomposepokedex.ui.theme.TypeFairy
import com.example.jetpackcomposepokedex.ui.theme.TypeFighting
import com.example.jetpackcomposepokedex.ui.theme.TypeFire
import com.example.jetpackcomposepokedex.ui.theme.TypeFlying
import com.example.jetpackcomposepokedex.ui.theme.TypeGhost
import com.example.jetpackcomposepokedex.ui.theme.TypeGrass
import com.example.jetpackcomposepokedex.ui.theme.TypeGround
import com.example.jetpackcomposepokedex.ui.theme.TypeIce
import com.example.jetpackcomposepokedex.ui.theme.TypeNormal
import com.example.jetpackcomposepokedex.ui.theme.TypePoison
import com.example.jetpackcomposepokedex.ui.theme.TypePsychic
import com.example.jetpackcomposepokedex.ui.theme.TypeRock
import com.example.jetpackcomposepokedex.ui.theme.TypeSteel
import com.example.jetpackcomposepokedex.ui.theme.TypeWater
import java.util.Locale

fun parseTypeToColor(type: Type): Color {
    return when(type.type.name.toLowerCase(Locale.ROOT)) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> Color.Black
    }
}

fun parseStatToColor(stat: Stat): Color {
    return when(stat.stat.name.toLowerCase()) {
        "hp" -> HPColor
        "attack" -> AtkColor
        "defense" -> DefColor
        "special-attack" -> SpAtkColor
        "special-defense" -> SpDefColor
        "speed" -> SpdColor
        else -> Color.White
    }
}

fun parseStatToAbbr(stat: Stat): String {
    return when(stat.stat.name.toLowerCase()) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> ""
    }
}