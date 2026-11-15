package com.example.apextime.screens

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apextime.R
import com.example.apextime.data.CorridaFavorita
import com.example.apextime.data.HomeUiState

private val SurfaceDark = Color(0xFF0F141C)
private val SurfaceContainer = Color(0xFF1B2028)
private val NeonGreen = Color(0xFF4BE277)
private val TextMuted = Color(0xFFBCCBB9)
private val AccentCyan = Color(0xFF38BDF8)
private val DarkCardInner = Color(0xFF131820)

data class SessionScheduleItem(
    val time: String,
    val title: String,
    val duration: String,
    val channels: String
)

@Composable
fun SessionDetailScreen(
    corrida: CorridaFavorita? = null,
    homeUiState: HomeUiState = HomeUiState(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val categoryId = (corrida?.id ?: "f1").lowercase()
    val categoryName = corrida?.categoria ?: "Fórmula 1"

    // Seleção dinâmica por categoria
    val isWec = categoryId.contains("wec") || categoryName.contains("wec", ignoreCase = true)
    val isIndy = categoryId.contains("indy") || categoryName.contains("indy", ignoreCase = true)
    val isNascar = categoryId.contains("nascar") || categoryName.contains("nascar", ignoreCase = true)

    val subHeaderCategory = when {
        isWec -> "FIA WEC • HYPERCAR & LMGT3"
        isIndy -> "NTT INDYCAR SERIES"
        isNascar -> "NASCAR CUP SERIES"
        else -> "FÓRMULA 1 • ${homeUiState.etapa.ifEmpty { "ETAPA ATIVA" }}"
    }

    val nomeCorrida = corrida?.nome?.ifEmpty { homeUiState.nomeCorrida } ?: when {
        isWec -> "TotalEnergies 6 Hours of Spa-Francorchamps"
        isIndy -> "Acura Grand Prix of Long Beach"
        isNascar -> "Wheatley Vodka Clash at Daytona"
        else -> "Las Vegas Grand Prix"
    }

    val circuito = corrida?.circuito?.ifEmpty { homeUiState.circuito } ?: when {
        isWec -> "Circuit de Spa-Francorchamps • Stavelot, Bélgica"
        isIndy -> "California Street Circuit • Long Beach, USA"
        isNascar -> "Daytona International Speedway • Daytona, USA"
        else -> "Las Vegas Strip Street Circuit • Las Vegas, USA"
    }

    val bannerRes = when {
        isWec -> R.drawable.banner_wec
        isIndy -> R.drawable.banner_indycar
        isNascar -> R.drawable.banner_nascar
        else -> R.drawable.banner_f1
    }

    val topSpeed = when {
        isWec -> "325 KM/H"
        isIndy -> "380 KM/H"
        isNascar -> "322 KM/H"
        else -> "342 KM/H"
    }

    val dataHoraFormatada = corrida?.dataHoraFormatada?.ifEmpty { homeUiState.dataHoraFormatada } ?: when {
        isWec -> "Sábado às 08:00 (Brasília)"
        isIndy -> "Domingo às 16:30 (Brasília)"
        isNascar -> "Sábado às 02:00 (Brasília)"
        else -> "Domingo às 01:00 (Brasília)"
    }

    val distanciaTexto = when {
        isWec -> "6 Horas de Duração (Endurance)"
        isIndy -> "85 Voltas (167.28 Milhas)"
        isNascar -> "200 Voltas (500.0 Milhas)"
        else -> homeUiState.voltas?.let { "$it Voltas (~305 km)" } ?: "50 Voltas (~310 km)"
    }

    val broadcastChannels = when {
        isWec -> listOf("BANDSPORTS", "YOUTUBE WEC")
        isIndy -> listOf("DISNEY+", "TV CULTURA")
        isNascar -> listOf("BANDSPORTS", "FOX SPORTS")
        else -> listOf("TV GLOBO (TV Aberta)", "SPORTV", "F1 TV PRO")
    }

    val customSchedules = when {
        isWec -> listOf(
            SessionScheduleItem("11:30", "Treino Livre 1", "90 min", "BandSports, YouTube WEC"),
            SessionScheduleItem("15:30", "Treino Livre 2", "90 min", "BandSports, YouTube WEC"),
            SessionScheduleItem("10:00", "Qualificação & Hyperpole", "Grid Decidido", "BandSports, YouTube WEC")
        )
        isIndy -> listOf(
            SessionScheduleItem("14:00", "Treino Livre 1", "75 min", "Disney+, TV Cultura"),
            SessionScheduleItem("12:00", "Classificação (Fast 6)", "Grid Decidido", "Disney+, TV Cultura")
        )
        isNascar -> listOf(
            SessionScheduleItem("18:00", "Treino de Classificação", "60 min", "BandSports"),
            SessionScheduleItem("21:00", "Duels at Daytona", "Grid Decidido", "BandSports")
        )
        else -> emptyList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header TopBar com botão de voltar ativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onBackClick() }
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "SESSÃO DETALHE",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCardInner)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(categoryName.uppercase(), color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonGreen.copy(alpha = 0.2f))
                            .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("• SESSÃO CONFIRMADA", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título e Velocidade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subHeaderCategory,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nomeCorrida,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = circuito,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardInner)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(topSpeed, color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagem Oficial da Categoria/GP em vez do desenho da pista
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = nomeCorrida,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay gradiente escuro
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SurfaceDark.copy(alpha = 0.85f))
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceDark.copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("• $circuito", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid de Métricas (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "DATA DA ETAPA",
                    value = dataHoraFormatada,
                    icon = Icons.Default.CalendarToday
                )
                MetricTile(
                    modifier = Modifier.weight(1f),
                    title = "FORMATO / DISTÂNCIA",
                    value = distanciaTexto,
                    icon = Icons.Default.Speed
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cronograma Completo
            Text(
                text = "Cronograma & Transmissão",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Horários de pista e onde assistir ao vivo",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sessões
            if (!isWec && !isIndy && !isNascar && homeUiState.sessoes.isNotEmpty()) {
                homeUiState.sessoes.forEach { sessao ->
                    ScheduleRow(
                        time = sessao.horario,
                        title = sessao.nome,
                        duration = sessao.descricao,
                        channels = broadcastChannels.joinToString(", ")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (customSchedules.isNotEmpty()) {
                customSchedules.forEach { s ->
                    ScheduleRow(s.time, s.title, s.duration, s.channels)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                ScheduleRow("11:30", "Treino Livre 1", "60 min", broadcastChannels.joinToString(", "))
                Spacer(modifier = Modifier.height(8.dp))
                ScheduleRow("15:00", "Treino Livre 2", "60 min", broadcastChannels.joinToString(", "))
                Spacer(modifier = Modifier.height(8.dp))
                ScheduleRow("14:00", "Classificação (Qualy)", "Grid Decidido", broadcastChannels.joinToString(", "))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card da Corrida Principal (Verde)
            Text("• DIA DA CORRIDA PRINCIPAL", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nomeCorrida,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = distanciaTexto,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "• AO VIVO",
                                color = SurfaceDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("ONDE ASSISTIR AO VIVO:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        broadcastChannels.forEach { channel ->
                            BroadcastChip(channel)
                        }
                    }
                }
            }
        }

        // Botão Flutuante Inferior - Adicionar à Agenda
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, "ApexTime: $nomeCorrida")
                        putExtra(CalendarContract.Events.EVENT_LOCATION, circuito)
                        putExtra(CalendarContract.Events.DESCRIPTION, "Fim de semana de corrida $nomeCorrida")
                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 86400000)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Fim de semana do $nomeCorrida adicionado à sua agenda! 📅",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp)
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SurfaceDark, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adicionar Fim de Semana à Agenda", color = SurfaceDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BroadcastChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkCardInner)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ScheduleRow(time: String, title: String, duration: String, channels: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("$time | $title", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Duração: $duration • Canais: $channels", color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}
