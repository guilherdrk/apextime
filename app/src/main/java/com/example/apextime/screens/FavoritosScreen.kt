package com.example.apextime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun FavoritosScreen(
    favoritosViewModel: FavoritosViewModel = viewModel(),
    onItemClick: (CorridaFavorita) -> Unit = {}
) {
    val favoritos by favoritosViewModel.favoritos.collectAsState()
    var corridaParaEditar by remember { mutableStateOf<CorridaFavorita?>(null) }

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
                        onItemClick = { onItemClick(favorito) },
                        onEditarAnotacao = { corridaParaEditar = favorito },
                        onRemover = { favoritosViewModel.remover(favorito.id) }
                    )
                }
            }
        }
    }

    corridaParaEditar?.let { corrida ->
        EditarAnotacaoDialog(
            corrida = corrida,
            onDismiss = { corridaParaEditar = null },
            onSalvar = { novaAnotacao ->
                favoritosViewModel.salvarAnotacao(corrida, novaAnotacao)
                corridaParaEditar = null
            }
        )
    }
}

@Composable
private fun FavoritoCard(
    favorito: CorridaFavorita,
    onItemClick: () -> Unit,
    onEditarAnotacao: () -> Unit,
    onRemover: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .clickable { onItemClick() }
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
            if (favorito.anotacaoPessoal.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PillBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📝 ${favorito.anotacaoPessoal}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEditarAnotacao) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar anotação",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PillBg)
                    .clickable { onRemover() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Remover dos favoritos",
                    tint = NeonGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EditarAnotacaoDialog(
    corrida: CorridaFavorita,
    onDismiss: () -> Unit,
    onSalvar: (String) -> Unit
) {
    var textoAnotacao by remember { mutableStateOf(corrida.anotacaoPessoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainer,
        title = {
            Text("Anotação Pessoal", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = corrida.nome,
                    color = NeonGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = textoAnotacao,
                    onValueChange = { textoAnotacao = it },
                    label = { Text("Adicionar nota / lembrete", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSalvar(textoAnotacao) }) {
                Text("Salvar", color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        }
    )
}
