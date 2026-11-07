package com.example.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

enum class WindowWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

@Immutable
data class AppDimensions(
    val widthClass: WindowWidthClass,
    val screenPaddingHorizontal: Dp,
    val screenPaddingVertical: Dp,
    val contentMaxWidth: Dp,
    val bottomNavMaxWidth: Dp,
    val cardCornerRadius: Dp,
    val cardCornerRadiusSmall: Dp,
    val cardPadding: Dp,
    val cardPaddingCompact: Dp,
    val gridSpacing: Dp,
    val sectionSpacing: Dp,
    val circularGaugeSize: Dp,
    val circularGaugeStroke: Dp,
    val speedometerDialSize: Dp,
    val speedometerDialStroke: Dp,
    val speedometerInnerWell: Dp,
    val headerIconBoxSize: Dp,
    val headerIconSize: Dp,
    val sparklineHeight: Dp,
    val throughputGraphHeight: Dp,
    val appItemIconSize: Dp,
    val isTablet: Boolean,
    val isExpanded: Boolean,
    // Scalable typography units
    val textDisplayHero: TextUnit,
    val textTitleLarge: TextUnit,
    val textTitleMedium: TextUnit,
    val textTitleSmall: TextUnit,
    val textMetricValue: TextUnit,
    val textBodyLarge: TextUnit,
    val textBodyMedium: TextUnit,
    val textCaption: TextUnit,
    val textMicro: TextUnit
)

val LocalAppDimensions = staticCompositionLocalOf {
    defaultAppDimensions()
}

fun defaultAppDimensions(): AppDimensions = AppDimensions(
    widthClass = WindowWidthClass.COMPACT,
    screenPaddingHorizontal = 16.dp,
    screenPaddingVertical = 8.dp,
    contentMaxWidth = 600.dp,
    bottomNavMaxWidth = 520.dp,
    cardCornerRadius = 22.dp,
    cardCornerRadiusSmall = 14.dp,
    cardPadding = 16.dp,
    cardPaddingCompact = 12.dp,
    gridSpacing = 10.dp,
    sectionSpacing = 14.dp,
    circularGaugeSize = 220.dp,
    circularGaugeStroke = 14.dp,
    speedometerDialSize = 240.dp,
    speedometerDialStroke = 14.dp,
    speedometerInnerWell = 130.dp,
    headerIconBoxSize = 44.dp,
    headerIconSize = 38.dp,
    sparklineHeight = 60.dp,
    throughputGraphHeight = 150.dp,
    appItemIconSize = 38.dp,
    isTablet = false,
    isExpanded = false,
    textDisplayHero = 24.sp,
    textTitleLarge = 22.sp,
    textTitleMedium = 16.sp,
    textTitleSmall = 14.sp,
    textMetricValue = 19.sp,
    textBodyLarge = 13.sp,
    textBodyMedium = 12.sp,
    textCaption = 11.sp,
    textMicro = 9.5.sp
)

/**
 * Reads dimension resources dynamically, adapting to sw600dp and sw720dp qualifiers,
 * while also factoring in container width constraints.
 */
@Composable
fun rememberAppDimensions(availableWidth: Dp? = null): AppDimensions {
    val screenPadH = dimensionResource(id = R.dimen.screen_padding_horizontal)
    val screenPadV = dimensionResource(id = R.dimen.screen_padding_vertical)
    val contentMaxW = dimensionResource(id = R.dimen.screen_content_max_width)
    val navMaxW = dimensionResource(id = R.dimen.bottom_nav_max_width)

    val cardCorner = dimensionResource(id = R.dimen.card_corner_radius)
    val cardCornerSmall = dimensionResource(id = R.dimen.card_corner_radius_small)
    val cardPad = dimensionResource(id = R.dimen.card_padding)
    val cardPadCompact = dimensionResource(id = R.dimen.card_padding_compact)
    val gridSpace = dimensionResource(id = R.dimen.grid_spacing)
    val sectionSpace = dimensionResource(id = R.dimen.section_spacing)

    val gaugeSize = dimensionResource(id = R.dimen.circular_gauge_size)
    val gaugeStroke = dimensionResource(id = R.dimen.circular_gauge_stroke)
    val speedDialSize = dimensionResource(id = R.dimen.speedometer_dial_size)
    val speedDialStroke = dimensionResource(id = R.dimen.speedometer_dial_stroke)
    val speedWell = dimensionResource(id = R.dimen.speedometer_inner_well)
    val headerBox = dimensionResource(id = R.dimen.header_icon_box_size)
    val headerIcon = dimensionResource(id = R.dimen.header_icon_size)
    val sparklineH = dimensionResource(id = R.dimen.sparkline_height)
    val throughputH = dimensionResource(id = R.dimen.throughput_graph_height)
    val appIcon = dimensionResource(id = R.dimen.app_item_icon_size)

    // Typography scalable dimensions
    val dispHero = dimensionResource(id = R.dimen.text_display_hero).value.sp
    val titleL = dimensionResource(id = R.dimen.text_title_large).value.sp
    val titleM = dimensionResource(id = R.dimen.text_title_medium).value.sp
    val titleS = dimensionResource(id = R.dimen.text_title_small).value.sp
    val metricVal = dimensionResource(id = R.dimen.text_metric_value).value.sp
    val bodyL = dimensionResource(id = R.dimen.text_body_large).value.sp
    val bodyM = dimensionResource(id = R.dimen.text_body_medium).value.sp
    val caption = dimensionResource(id = R.dimen.text_caption).value.sp
    val micro = dimensionResource(id = R.dimen.text_micro).value.sp

    val widthDp = availableWidth ?: screenPadH * 2 + 360.dp
    val widthClass = when {
        widthDp >= 840.dp -> WindowWidthClass.EXPANDED
        widthDp >= 600.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.COMPACT
    }

    val isTablet = widthClass != WindowWidthClass.COMPACT || screenPadH >= 24.dp
    val isExpanded = widthClass == WindowWidthClass.EXPANDED || screenPadH >= 36.dp

    return AppDimensions(
        widthClass = widthClass,
        screenPaddingHorizontal = screenPadH,
        screenPaddingVertical = screenPadV,
        contentMaxWidth = contentMaxW,
        bottomNavMaxWidth = navMaxW,
        cardCornerRadius = cardCorner,
        cardCornerRadiusSmall = cardCornerSmall,
        cardPadding = cardPad,
        cardPaddingCompact = cardPadCompact,
        gridSpacing = gridSpace,
        sectionSpacing = sectionSpace,
        circularGaugeSize = gaugeSize,
        circularGaugeStroke = gaugeStroke,
        speedometerDialSize = speedDialSize,
        speedometerDialStroke = speedDialStroke,
        speedometerInnerWell = speedWell,
        headerIconBoxSize = headerBox,
        headerIconSize = headerIcon,
        sparklineHeight = sparklineH,
        throughputGraphHeight = throughputH,
        appItemIconSize = appIcon,
        isTablet = isTablet,
        isExpanded = isExpanded,
        textDisplayHero = dispHero,
        textTitleLarge = titleL,
        textTitleMedium = titleM,
        textTitleSmall = titleS,
        textMetricValue = metricVal,
        textBodyLarge = bodyL,
        textBodyMedium = bodyM,
        textCaption = caption,
        textMicro = micro
    )
}

/**
 * Adaptive content container that centers content and enforces max width on tablets and wide screens,
 * preventing awkward horizontal stretching while preserving fluid responsiveness on phones.
 */
@Composable
fun AdaptiveScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable (AppDimensions) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val dimensions = rememberAppDimensions(availableWidth = maxWidth)
        CompositionLocalProvider(LocalAppDimensions provides dimensions) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = dimensions.contentMaxWidth)
                ) {
                    content(dimensions)
                }
            }
        }
    }
}
