package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLiquidGlass

/**
 * GPU-accelerated tactile spring physics modifier for cards, pills, and interactive surfaces.
 * Uses [graphicsLayer] scaling driven by [animateFloatAsState] + spring damping so 60/120fps
 * touch interactions never trigger relayout or recomposition.
 */
fun Modifier.tactileSpringPress(
    pressedScale: Float = 0.965f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (enabled && isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.56f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactile_spring_press"
    )
    val base = this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
    if (onClick != null) {
        base.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
    } else {
        base
    }
}

/**
 * Modifier that renders modern Neumorphic (Soft UI) dual shadows:
 * - A diffuse lighter highlight shadow on top-left (-offset, -offset)
 * - A deep ambient dark shadow on bottom-right (+offset, +offset)
 * When [isPressed] is true, it transforms into an indented (pressed-in) well.
 */
fun Modifier.neumorphicDepth(
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    lightColor: Color = Color(0x384E699A),
    darkColor: Color = Color(0xE603050B),
    offsetDp: Dp = 4.dp,
    isPressed: Boolean = false
): Modifier = this.drawBehind {
    val cornerRadius = shape.topStart.toPx(size, this)
    val offset = offsetDp.toPx()

    if (!isPressed) {
        // 1. Top-Left Soft Highlight Glow
        drawRoundRect(
            color = lightColor,
            topLeft = Offset(-offset, -offset),
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // 2. Bottom-Right Deep Ambient Shadow
        drawRoundRect(
            color = darkColor,
            topLeft = Offset(offset, offset),
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )
    } else {
        // Indented / Pressed: Dark inner shadow at top-left, soft light at bottom-right
        drawRoundRect(
            color = darkColor.copy(alpha = 0.7f),
            topLeft = Offset(-1f, -1f),
            size = Size(size.width + 2f, size.height + 2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )
    }
}

/**
 * Neumorphic Card container with soft extruded surface, dual shadows,
 * and specular bevel border.
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    isPressed: Boolean = false,
    accentBorder: Color? = null,
    backgroundColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val surfaceBase = backgroundColor ?: glassStyle.neumorphicSurface
    val lightShadow = glassStyle.neumorphicHighlight
    val darkShadow = glassStyle.neumorphicShadow

    // Dual-tone border: top-left specular highlight, bottom-right ambient shadow
    val bevelBorderBrush = if (accentBorder != null) {
        Brush.linearGradient(
            listOf(accentBorder.copy(alpha = 0.7f), glassStyle.neumorphicBorderLight, accentBorder.copy(alpha = 0.3f)),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    } else if (isPressed) {
        Brush.linearGradient(
            listOf(glassStyle.neumorphicBorderDark, glassStyle.neumorphicBorderLight.copy(alpha = 0.15f)),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    } else {
        Brush.linearGradient(
            listOf(glassStyle.neumorphicBorderLight, glassStyle.neumorphicBorderDark),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    }

    // Extruded convex gradient vs Indented concave gradient
    val surfaceBrush = if (isPressed) {
        Brush.linearGradient(
            colors = listOf(
                glassStyle.cardSubtleBg,
                surfaceBase.copy(alpha = 0.85f)
            ),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                surfaceBase.copy(alpha = 0.98f),
                glassStyle.neumorphicBase.copy(alpha = 0.95f)
            ),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    }

    Box(
        modifier = modifier
            .neumorphicDepth(
                shape = shape,
                lightColor = lightShadow,
                darkColor = darkShadow,
                isPressed = isPressed
            )
            .clip(shape)
            .background(surfaceBrush)
            .border(width = 1.dp, brush = bevelBorderBrush, shape = shape)
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * Interactive Neumorphic Button that depresses into an indented state on click
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    accentColor: Color? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.955f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.56f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btn_scale"
    )

    val effectiveAccent = accentColor ?: glassStyle.accentPrimary

    val buttonBrush = if (isPressed) {
        Brush.linearGradient(
            listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
        )
    } else {
        Brush.linearGradient(
            listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase)
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphicDepth(
                shape = shape,
                lightColor = glassStyle.neumorphicHighlight,
                darkColor = glassStyle.neumorphicShadow,
                isPressed = isPressed
            )
            .clip(shape)
            .background(buttonBrush)
            .border(
                width = 1.dp,
                brush = if (isPressed) {
                    Brush.linearGradient(listOf(effectiveAccent.copy(alpha = 0.8f), glassStyle.neumorphicBorderDark))
                } else {
                    Brush.linearGradient(listOf(glassStyle.neumorphicBorderLight, effectiveAccent.copy(alpha = 0.35f)))
                },
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Neumorphic Icon Button with circular or squircle shape and tactile press
 */
@Composable
fun NeumorphicIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    tint: Color? = null,
    isHighlighted: Boolean = false,
    sizeDp: Dp = 40.dp
) {
    val glassStyle = LocalLiquidGlass.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )

    val iconTint = tint ?: if (isHighlighted) glassStyle.accentPrimary else glassStyle.textPrimary

    val roundedShape = shape as? RoundedCornerShape ?: RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphicDepth(
                shape = roundedShape,
                lightColor = glassStyle.neumorphicHighlight,
                darkColor = glassStyle.neumorphicShadow,
                isPressed = isPressed || isHighlighted
            )
            .clip(shape)
            .background(
                if (isPressed || isHighlighted) {
                    Brush.linearGradient(listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface))
                } else {
                    Brush.linearGradient(listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase))
                }
            )
            .border(
                1.dp,
                if (isHighlighted) glassStyle.accentPrimary.copy(alpha = 0.5f) else glassStyle.neumorphicBorderLight,
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size((sizeDp.value * 0.52f).dp)
        )
    }
}

/**
 * Neumorphic Segmented Tab/Pill.
 * Unselected: Gently rests on the surface (extruded/flat).
 * Selected: Deep indented (pressed-in) well with neon glow and high-contrast text.
 */
@Composable
fun NeumorphicSegmentedPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accentColor: Color? = null
) {
    val glassStyle = LocalLiquidGlass.current
    val effectiveAccent = accentColor ?: glassStyle.accentPrimary
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .neumorphicDepth(
                shape = shape,
                lightColor = if (isSelected) Color.Transparent else glassStyle.neumorphicHighlight.copy(alpha = 0.5f),
                darkColor = if (isSelected) glassStyle.neumorphicShadow else glassStyle.neumorphicShadow.copy(alpha = 0.6f),
                isPressed = isSelected
            )
            .clip(shape)
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        listOf(
                            glassStyle.cardSubtleBg,
                            effectiveAccent.copy(alpha = 0.16f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            glassStyle.neumorphicSurface.copy(alpha = 0.8f),
                            glassStyle.neumorphicBase.copy(alpha = 0.8f)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = if (isSelected) {
                    Brush.linearGradient(listOf(effectiveAccent.copy(alpha = 0.8f), glassStyle.neumorphicBorderDark))
                } else {
                    Brush.linearGradient(listOf(glassStyle.neumorphicBorderLight.copy(alpha = 0.2f), glassStyle.neumorphicBorderDark))
                },
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) effectiveAccent else glassStyle.textTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                color = if (isSelected) effectiveAccent else glassStyle.textSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

/**
 * Soft Tactile Neumorphic Switch with indented track and extruded sliding thumb
 */
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color? = null
) {
    val glassStyle = LocalLiquidGlass.current
    val effectiveAccent = accentColor ?: glassStyle.accentPrimary

    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 24f else 0f,
        animationSpec = spring(
            dampingRatio = 0.62f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "switch_thumb"
    )

    // Indented track
    Box(
        modifier = modifier
            .size(width = 52.dp, height = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (checked) {
                    Brush.horizontalGradient(listOf(effectiveAccent.copy(alpha = 0.25f), effectiveAccent.copy(alpha = 0.45f)))
                } else {
                    Brush.linearGradient(listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface))
                }
            )
            .border(
                1.dp,
                if (checked) effectiveAccent.copy(alpha = 0.5f) else glassStyle.neumorphicBorderDark,
                RoundedCornerShape(14.dp)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Extruded tactile thumb
        Box(
            modifier = Modifier
                .padding(start = thumbOffset.dp)
                .size(22.dp)
                .neumorphicDepth(
                    shape = CircleShape,
                    lightColor = if (checked) effectiveAccent.copy(alpha = 0.5f) else glassStyle.neumorphicHighlight,
                    darkColor = glassStyle.neumorphicShadow,
                    offsetDp = 2.dp
                )
                .clip(CircleShape)
                .background(
                    if (checked) effectiveAccent else Color(0xFFD1D5DB)
                )
                .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
        )
    }
}
