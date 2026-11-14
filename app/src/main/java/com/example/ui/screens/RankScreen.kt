package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.AppUsageItem
import com.example.model.FormatUtils
import com.example.ui.MainViewModel
import com.example.ui.components.ElasticFlamePullToRefreshBox
import com.example.ui.components.GlassContainer
import com.example.ui.components.UsagePermissionCard
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalAppDimensions
import com.example.ui.theme.LocalLiquidGlass

private val GoldChampion = Color(0xFFFFD700)
private val GoldAccent = Color(0xFFFFA000)
private val SilverChampion = Color(0xFFE2E8F0)
private val SilverAccent = Color(0xFF94A3B8)
private val BronzeChampion = Color(0xFFF59E0B)
private val BronzeAccent = Color(0xFFB45309)

@Composable
fun RankScreen(
    viewModel: MainViewModel,
    onRequestUsagePermission: () -> Unit,
    onOpenAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rankedApps by viewModel.allTimeRankedApps.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRankRefreshing.collectAsStateWithLifecycle()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val glassStyle = LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current

    ElasticFlamePullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshRankings() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.screenPaddingHorizontal),
            contentPadding = PaddingValues(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Screen Title & All-Time Competition Banner
            item {
                RankHeader(
                    appLanguage = appLanguage,
                    onRefresh = { viewModel.refreshRankings() }
                )
            }

            // Usage Permission Warning Card if not granted
            if (!hasUsagePerm) {
                item {
                    UsagePermissionCard(
                        onRequestPermission = onRequestUsagePermission,
                        onOpenAppInfo = onOpenAppInfo,
                        appLanguage = appLanguage
                    )
                }
            } else if (rankedApps.isEmpty()) {
                // Empty / Initializing State
                item {
                    GlassContainer(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = glassStyle.accentPrimary.copy(alpha = 0.45f),
                        enableBreathingGlow = true,
                        glowColor = glassStyle.accentPrimary
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldChampion,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "جاري احتساب استهلاك التطبيقات على مر التاريخ..." else "Calculating all-time application consumption...",
                                color = glassStyle.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (appLanguage == AppLanguage.ARABIC) "اسحب للأسفل للتحديث واحتساب النتائج فوراً" else "Pull down to refresh and compute rankings",
                                color = glassStyle.textTertiary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // 1. Top 3 Champions Podium (الرتبة الأولى، الثانية، الثالثة)
                item {
                    TopChampionsPodium(
                        apps = rankedApps.take(3),
                        appLanguage = appLanguage
                    )
                }

                // 2. Leaderboard Title for Remaining Applications (وباقي التطبيقات اسفله)
                if (rankedApps.size > 3) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = CyanPulse,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.rankRemaining(appLanguage),
                                    color = glassStyle.textPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(glassStyle.cardSubtleBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${rankedApps.size - 3}",
                                    color = CyanPulse,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Remaining apps starting from rank 4
                    val leaderBytes = rankedApps.first().totalBytes.coerceAtLeast(1L)
                    itemsIndexed(rankedApps.drop(3), key = { _, item -> item.packageName }) { index, item ->
                        val rankPosition = index + 4
                        RankedAppRow(
                            rank = rankPosition,
                            item = item,
                            leaderBytes = leaderBytes,
                            appLanguage = appLanguage
                        )
                    }
                }
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
fun RankHeader(
    appLanguage: AppLanguage,
    onRefresh: () -> Unit
) {
    val glassStyle = LocalLiquidGlass.current

    GlassContainer(
        modifier = Modifier.fillMaxWidth(),
        borderColor = GoldChampion.copy(alpha = 0.40f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldChampion.copy(alpha = 0.25f), GoldAccent.copy(alpha = 0.12f))
                                )
                            )
                            .border(1.dp, GoldChampion.copy(alpha = 0.50f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = GoldChampion,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppStrings.rankTitle(appLanguage),
                                color = glassStyle.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldChampion.copy(alpha = 0.18f))
                                    .border(0.8.dp, GoldChampion.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = AppStrings.rankAllTimeBadge(appLanguage),
                                    color = GoldChampion,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppStrings.rankSubtitle(appLanguage),
                            color = glassStyle.textSecondary,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(32.dp).testTag("refresh_rank_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = AppStrings.refresh(appLanguage),
                        tint = GoldChampion,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric explanation banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassStyle.cardSubtleBg)
                    .border(0.8.dp, glassStyle.borderColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🏆 " + AppStrings.rankTotalCombined(appLanguage),
                        color = glassStyle.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SignalCellularAlt, null, tint = CyanPulse, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+", color = glassStyle.textTertiary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Default.Wifi, null, tint = ElectricBlue, modifier = Modifier.size(11.dp))
                    }
                }
            }
        }
    }
}

/**
 * Top 3 Champions Podium Showcase:
 * - Center: Rank 1 (Gold 🥇)
 * - Left: Rank 2 (Silver 🥈)
 * - Right: Rank 3 (Bronze 🥉)
 */
@Composable
fun TopChampionsPodium(
    apps: List<AppUsageItem>,
    appLanguage: AppLanguage
) {
    if (apps.isEmpty()) return
    val glassStyle = LocalLiquidGlass.current

    GlassContainer(
        modifier = Modifier.fillMaxWidth(),
        borderColor = GoldChampion.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = GoldChampion,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppStrings.rankPodiumTitle(appLanguage),
                    color = GoldChampion,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Tier Podium Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Silver: Rank 2
                if (apps.size >= 2) {
                    PodiumPillar(
                        rank = 2,
                        app = apps[1],
                        pillarHeight = 130.dp,
                        accentColor = SilverChampion,
                        gradientColor = SilverAccent,
                        medalEmoji = "🥈",
                        rankLabel = AppStrings.rankSecond(appLanguage),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Gold: Rank 1 (Elevated)
                if (apps.isNotEmpty()) {
                    PodiumPillar(
                        rank = 1,
                        app = apps[0],
                        pillarHeight = 158.dp,
                        accentColor = GoldChampion,
                        gradientColor = GoldAccent,
                        medalEmoji = "🥇",
                        rankLabel = AppStrings.rankFirst(appLanguage),
                        modifier = Modifier.weight(1.15f)
                    )
                }

                // Bronze: Rank 3
                if (apps.size >= 3) {
                    PodiumPillar(
                        rank = 3,
                        app = apps[2],
                        pillarHeight = 118.dp,
                        accentColor = BronzeChampion,
                        gradientColor = BronzeAccent,
                        medalEmoji = "🥉",
                        rankLabel = AppStrings.rankThird(appLanguage),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PodiumPillar(
    rank: Int,
    app: AppUsageItem,
    pillarHeight: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    gradientColor: Color,
    medalEmoji: String,
    rankLabel: String,
    modifier: Modifier = Modifier
) {
    val glassStyle = LocalLiquidGlass.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon with Champion Halo
        Box(
            modifier = Modifier
                .size(if (rank == 1) 48.dp else 42.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(accentColor.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
                .border(if (rank == 1) 2.dp else 1.5.dp, accentColor, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            AppIconBitmap(drawable = app.icon, appName = app.appName)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // App Name
        Text(
            text = app.appName,
            color = glassStyle.textPrimary,
            fontSize = if (rank == 1) 12.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // Total Combined Bytes (Cellular + Wi-Fi)
        Text(
            text = FormatUtils.formatBytes(app.totalBytes),
            color = accentColor,
            fontSize = if (rank == 1) 12.sp else 10.5.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = if (rank == 1) 0.30f else 0.20f),
                            gradientColor.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(
                    1.dp,
                    accentColor.copy(alpha = if (rank == 1) 0.65f else 0.40f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Medal Emoji & Rank Number
                Text(
                    text = "$medalEmoji #$rank",
                    color = accentColor,
                    fontSize = if (rank == 1) 15.sp else 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = rankLabel,
                    color = glassStyle.textSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // Breakdown pills at bottom of pedestal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "📶 " + FormatUtils.formatBytes(app.mobileBytes),
                        color = glassStyle.textTertiary,
                        fontSize = 8.5.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "📡 " + FormatUtils.formatBytes(app.wifiBytes),
                        color = glassStyle.textTertiary,
                        fontSize = 8.5.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Row card for applications ranked #4 and beyond.
 */
@Composable
fun RankedAppRow(
    rank: Int,
    item: AppUsageItem,
    leaderBytes: Long,
    appLanguage: AppLanguage
) {
    val glassStyle = LocalLiquidGlass.current
    val dimensions = LocalAppDimensions.current

    GlassContainer(
        modifier = Modifier.fillMaxWidth(),
        borderColor = glassStyle.borderColor
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank position badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(glassStyle.cardSubtleBg)
                        .border(1.dp, glassStyle.borderColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$rank",
                        color = glassStyle.textSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // App Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AppIconBitmap(drawable = item.icon, appName = item.appName)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // App Name and package
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.appName,
                        color = glassStyle.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.packageName,
                        color = glassStyle.textTertiary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Total combined data
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatBytes(item.totalBytes),
                        color = glassStyle.accentPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "%.1f%%".format(item.percentageOfTotal * 100f),
                        color = glassStyle.textTertiary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detailed Wi-Fi & Cellular breakdown pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CellTower,
                        contentDescription = null,
                        tint = CyanPulse,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${AppStrings.filterCellular(appLanguage)}: ${FormatUtils.formatBytes(item.mobileBytes)}",
                        color = glassStyle.textSecondary,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${AppStrings.filterWifi(appLanguage)}: ${FormatUtils.formatBytes(item.wifiBytes)}",
                        color = glassStyle.textSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Relative proportion bar compared to the #1 all-time champion app
            val relativeFraction = (item.totalBytes.toFloat() / leaderBytes.toFloat()).coerceIn(0.01f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (glassStyle.isDark) Color(0xFF0D1524) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(relativeFraction)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(glassStyle.accentSecondary, glassStyle.accentPrimary)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AppIconBitmap(drawable: Drawable?, appName: String) {
    val bitmap = remember(drawable) {
        drawable?.let { d ->
            try {
                if (d is BitmapDrawable && d.bitmap != null) {
                    d.bitmap
                } else {
                    val w = d.intrinsicWidth.coerceAtLeast(1)
                    val h = d.intrinsicHeight.coerceAtLeast(1)
                    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    d.setBounds(0, 0, canvas.width, canvas.height)
                    d.draw(canvas)
                    bmp
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = appName,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = appName.take(1).uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
