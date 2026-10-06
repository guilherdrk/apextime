package com.example.apextime.data

import androidx.annotation.DrawableRes

data class NextStageInfo(
    val roundCode: String,
    val title: String,
    val dateRange: String
)

data class CategorySeries(
    val id: String,
    val name: String,
    val division: String,
    val statusTag: String,
    val isLiveOrActive: Boolean = false,
    val bannerImageUrl: String? = null,
    @param:DrawableRes val bannerDrawableRes: Int? = null,
    val bannerLocationTag: String,
    val metaTags: List<String>,
    val nextStage: NextStageInfo
)

enum class CategoryFilterTab(val label: String) {
    TEMPORADAS_ATIVAS("TEMPORADAS ATIVAS"),
    A_SEGUIR("A SEGUIR")
}

data class CategoriesUiState(
    val activeFilter: CategoryFilterTab = CategoryFilterTab.TEMPORADAS_ATIVAS,
    val totalHomologatedSeries: Int = 4,
    val categories: List<CategorySeries> = emptyList(),
    val isLoading: Boolean = false
)
