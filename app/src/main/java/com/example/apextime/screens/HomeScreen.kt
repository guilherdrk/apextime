package com.example.apextime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.apextime.data.HomeUiState
import com.example.apextime.data.HomeViewModel
import com.example.apextime.data.NascarUiState
import com.example.apextime.data.NascarViewModel
import com.example.apextime.data.SessaoUi
import java.time.LocalTime

/* ---------- Cores extras (as demais vêm do MainActivity.kt: SurfaceDark, SurfaceContainer, NeonGreen, TextMuted) ---------- */
val PillBg = Color(0xFF12161D)
val AccentBlue = Color(0xFF4D8DFF)

private val categorias = listOf("F1", "IndyCar", "WEC", "NASCAR")

/** Bom dia (05h–11h59), Boa tarde (12h–17h59) ou Boa noite (18h–04h59), pelo horário do aparelho. */
private fun saudacaoPorHorario(): String {
    val hora = LocalTime.now().hour
    return when (hora) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
}

@Composable
fun ApexTimeHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    nascarViewModel: NascarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val nascarUiState by nascarViewModel.uiState.collectAsState()
    var categoriaSelecionada by remember { mutableStateOf("F1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        HeaderRow(
            titulo = "APEXTIME",
            subtitulo = "INÍCIO",
            trailing = { Icon(Icons.Filled.Wifi, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp)) }
        )
        Spacer(Modifier.height(10.dp))
        HeaderRow(
            titulo = "PIT WALL",
            subtitulo = "LIVE TELEMETRY",
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.White, modifier = Modifier.size(18.dp))
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificações", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        )

        Spacer(Modifier.height(18.dp))
        val saudacao = remember { saudacaoPorHorario() }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.RocketLaunch, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("$saudacao, Fã de Velocidade", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "Próximas largadas do fim de semana",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 24.dp, top = 2.dp)
        )

        Spacer(Modifier.height(12.dp))
        CategoryTabs(selecionada = categoriaSelecionada, onSelecionar = { categoriaSelecionada = it })

        Spacer(Modifier.height(14.dp))

        when (categoriaSelecionada) {
            "F1" -> {
                when {
                    uiState.carregando -> CarregandoCard()
                    uiState.erro != null -> ErroCard(mensagem = uiState.erro!!, onTentarNovamente = viewModel::tentarNovamente)
                    else -> RaceCard(uiState)
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Sessões do Fim de Semana", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Text("${uiState.sessoes.size} Sessões", color = TextMuted, fontSize = 12.sp)
                }

                Spacer(Modifier.height(12.dp))
                uiState.sessoes.forEach { sessao ->
                    SessionItem(sessao)
                    Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(10.dp))
                TrackRecordCard(uiState)
            }
            "NASCAR" -> {
                when {
                    nascarUiState.carregando -> CarregandoCard()
                    nascarUiState.erro != null -> ErroCard(mensagem = nascarUiState.erro!!, onTentarNovamente = nascarViewModel::tentarNovamente)
                    else -> NascarRaceCard(nascarUiState)
                }
            }
            else -> EmBreveCard(categoriaSelecionada)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun HeaderRow(titulo: String, subtitulo: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#", color = NeonGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(titulo, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Text(subtitulo, color = TextMuted, fontSize = 9.sp, letterSpacing = 0.5.sp)
            }
        }
        trailing()
    }
}

@Composable
private fun CategoryTabs(selecionada: String, onSelecionar: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { categoria ->
            val ativo = categoria == selecionada
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (ativo) SurfaceContainer else PillBg)
                    .clickable { onSelecionar(categoria) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (ativo) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = categoria,
                    color = if (ativo) Color.White else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (ativo) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CarregandoCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = NeonGreen, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun ErroCard(mensagem: String, onTentarNovamente: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(mensagem, color = TextMuted, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onTentarNovamente,
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
        ) {
            Text("Tentar novamente", color = SurfaceDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RaceCard(uiState: HomeUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                )
                Spacer(Modifier.width(6.dp))
                Text("FÓRMULA 1 • ${uiState.etapa}", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentBlue.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("AUTÓDROMO", color = AccentBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(uiState.nomeCorrida, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("${uiState.circuito} • ${uiState.localCircuito}", color = TextMuted, fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PillBg)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("LAPS / CLIMA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = uiState.voltas?.let { "$it Voltas" } ?: "-- Voltas",
                    color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium
                )
            }
            if (uiState.climaCarregando) {
                CircularProgressIndicator(color = NeonGreen, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.WbSunny, contentDescription = null, tint = Color(0xFFFFC94D), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("${uiState.temperaturaTexto} ${uiState.condicaoPista}", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("CONTAGEM REGRESSIVA", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountdownUnit("%02d".format(uiState.dias), "DIAS", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.horas), "HORAS", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.minutos), "MIN", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.segundos), "SEG", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))
        Text(uiState.dataHoraFormatada, color = TextMuted, fontSize = 12.sp)

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BroadcastChip("BAND")
            BroadcastChip("F1 TV PRO")
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { /* TODO: definir lembrete */ },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = SurfaceDark, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Definir Lembrete", color = SurfaceDark, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PillBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.StarBorder, contentDescription = "Favoritar", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun NascarRaceCard(uiState: NascarUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(NeonGreen)
            )
            Spacer(Modifier.width(6.dp))
            Text("NASCAR CUP SERIES", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))
        Text(uiState.nomeCorrida, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(uiState.circuito, color = TextMuted, fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PillBg)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("VOLTAS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(uiState.voltas?.toString() ?: "--", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("DISTÂNCIA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(uiState.distanciaTexto, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("CONTAGEM REGRESSIVA", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CountdownUnit("%02d".format(uiState.dias), "DIAS", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.horas), "HORAS", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.minutos), "MIN", Modifier.weight(1f))
            CountdownUnit("%02d".format(uiState.segundos), "SEG", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))
        Text(uiState.dataHoraFormatada, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
private fun EmBreveCard(categoria: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainer)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Dados de $categoria em breve", color = TextMuted, fontSize = 13.sp)
    }
}

@Composable
private fun CountdownUnit(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PillBg)
            .padding(vertical = 10.dp)
    ) {
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BroadcastChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PillBg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SessionItem(sessao: SessaoUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (sessao.destaque) NeonGreen else TextMuted)
            )
            Spacer(Modifier.width(6.dp))
            Text(sessao.tag, color = if (sessao.destaque) NeonGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(sessao.nome, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(sessao.horario, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(sessao.descricao, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
private fun TrackRecordCard(uiState: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PillBg),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.recordeCarregando) {
                CircularProgressIndicator(color = NeonGreen, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Icon(Icons.Filled.Star, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("RECORDE DA PISTA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(uiState.recordeTempo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (uiState.recordeAno.isNotEmpty()) "${uiState.recordePiloto} (${uiState.recordeAno})" else uiState.recordePiloto,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AccentBlue.copy(alpha = 0.18f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text("DRS x2 ✓", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}