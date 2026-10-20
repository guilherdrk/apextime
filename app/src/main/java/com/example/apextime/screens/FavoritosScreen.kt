package com.example.apextime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apextime.NeonGreen
import com.example.apextime.PillBg
import com.example.apextime.SurfaceContainer
import com.example.apextime.SurfaceDark
import com.example.apextime.TextMuted
import com.example.apextime.data.CorridaFavorita
import com.example.apextime.data.FavoritosViewModel

@Composable
fun FavoritosScreen(favoritosViewModel: FavoritosViewModel = viewModel()) {
    val favoritos by favoritosViewModel.favoritos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Favoritos", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "Suas largadas marcadas pra não perder",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 28.dp, top = 2.dp)
        )

        Spacer(Modifier.height(20.dp))

        if (favoritos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Nenhum favorito ainda",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Toque na estrela de uma corrida na Início para marcá-la aqui",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(favoritos, key = { it.id }) { favorito ->
                    FavoritoCard(
                        favorito = favorito,
                        onRemover = { favoritosViewModel.remover(favorito.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritoCard(favorito: CorridaFavorita, onRemover: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(favorito.categoria, color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(favorito.nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(favorito.circuito, color = TextMuted, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(favorito.dataHoraFormatada, color = TextMuted, fontSize = 12.sp)
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PillBg)
                .clickable { onRemover() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Star, contentDescription = "Remover dos favoritos", tint = NeonGreen, modifier = Modifier.size(18.dp))
        }
    }
}