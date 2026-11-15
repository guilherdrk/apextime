package com.example.apextime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apextime.data.CorridaFavorita
import com.example.apextime.data.HomeViewModel
import com.example.apextime.screens.CategoriesScreen
import com.example.apextime.screens.FavoritosScreen
import com.example.apextime.screens.SessionDetailScreen
import com.example.apextime.ui.theme.ApexTimeTheme

// Cores base extraídas do protótipo
val SurfaceDark = Color(0xFF0F141C)
val SurfaceContainer = Color(0xFF1B2028)
val NeonGreen = Color(0xFF4BE277)
val TextMuted = Color(0xFFBCCBB9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ApexTimeTheme {
                ApexTimeApp()
            }
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("Início", Icons.Default.SportsScore)
    data object Categories : BottomNavItem("Categorias", Icons.Default.EmojiEvents)
    data object Favorites : BottomNavItem("Favoritos", Icons.Default.Star)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApexTimeApp(homeViewModel: HomeViewModel = viewModel()) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val items = listOf(BottomNavItem.Home, BottomNavItem.Categories, BottomNavItem.Favorites)
    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedCorridaDetail by remember { mutableStateOf<CorridaFavorita?>(null) }
    var showDetailScreen by remember { mutableStateOf(false) }

    if (showDetailScreen) {
        SessionDetailScreen(
            corrida = selectedCorridaDetail,
            homeUiState = homeUiState,
            onBackClick = { showDetailScreen = false }
        )
    } else {
        Scaffold(
            containerColor = SurfaceDark,
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceContainer,
                    tonalElevation = 8.dp
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SurfaceDark,
                                selectedTextColor = NeonGreen,
                                indicatorColor = NeonGreen,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SurfaceDark),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedIndex) {
                    0 -> ApexTimeHomeScreen(
                        viewModel = homeViewModel,
                        onRaceCardClick = { corrida ->
                            selectedCorridaDetail = corrida
                            showDetailScreen = true
                        }
                    )
                    1 -> CategoriesScreen(
                        onBackClick = { selectedIndex = 0 },
                        onRaceCardClick = { corrida ->
                            selectedCorridaDetail = corrida
                            showDetailScreen = true
                        }
                    )
                    2 -> FavoritosScreen(
                        onItemClick = { favorito ->
                            selectedCorridaDetail = favorito
                            showDetailScreen = true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApexTimeAppPreview() {
    ApexTimeTheme {
        ApexTimeApp()
    }
}
