package com.example.ui.components

import android.graphics.Paint as NativePaint
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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
 * Modifier that renders modern Neumorphic (Soft UI) dual shadows via [drawBehind]:
 * - A soft lighter highlight shadow on top-left (-offset, -offset)
 * - A deep ambient dark shadow on bottom-right (+offset, +offset)
 * When [surfaceColor] is specified (e.g. `#EEF2F6` in the Neumorphism theme), it renders smooth
 * blurred shadow layers using native `setShadowLayer`; when [isPressed] is true, it renders an
 * indented pressed-in well.
 */
fun Modifier.neumorphicDepth(
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    lightColor: Color = Color(0x384E699A),
    darkColor: Color = Color(0xE603050B),
    offsetDp: Dp = 4.dp,
    isPressed: Boolean = false,
    surfaceColor: Color = Color.Unspecified,
    blurDp: Dp = 10.dp
): Modifier = this.drawBehind {
    val cornerRadius = shape.topStart.toPx(size, this)
    val offset = offsetDp.toPx()
    val blurPx = blurDp.toPx().coerceAtLeast(1f)

    if (!isPressed) {
        if (surfaceColor != Color.Unspecified && surfaceColor.alpha >= 0.9f) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val lightPaint = NativePaint().apply {
                    isAntiAlias = true
                    color = surfaceColor.toArgb()
                    setShadowLayer(blurPx, -offset, -offset, lightColor.toArgb())
                }
                val darkPaint = NativePaint().apply {
                    isAntiAlias = true
                    color = surfaceColor.toArgb()
                    setShadowLayer(blurPx, offset, offset, darkColor.toArgb())
                }
                nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height,
                    cornerRadius, cornerRadius,
                    darkPaint
                )
                nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height,
                    cornerRadius, cornerRadius,
                    lightPaint
                )
            }
        } else {
            // Translucent glass dual shadow fallback
            drawRoundRect(
                color = lightColor,
                topLeft = Offset(-offset, -offset),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
            drawRoundRect(
                color = darkColor,
                topLeft = Offset(offset, offset),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    } else {
        // Indented / Pressed: Subtle dark rim along top-left & light rim along bottom-right
        drawRoundRect(
            color = darkColor.copy(alpha = if (surfaceColor != Color.Unspecified) 0.55f else 0.7f),
            topLeft = Offset(-1.5f, -1.5f),
            size = Size(size.width + 3f, size.height + 3f),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
        drawRoundRect(
            color = lightColor.copy(alpha = 0.65f),
            topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

/**
 * Neumorphic Card container with custom `drawBehind` dual-shadow canvas rendering,
 * soft extruded or pressed depth, and clean specular bevel border.
 */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    isPressed: Boolean = false,
    accentBorder: Color? = null,
    backgroundColor: Color? = null,
    elevationOffset: Dp = 6.dp,
    shadowBlur: Dp = 12.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val surfaceBase = backgroundColor ?: glassStyle.neumorphicSurface
    val lightShadow = glassStyle.neumorphicHighlight
    val darkShadow = glassStyle.neumorphicShadow

    val bevelBorderBrush = if (accentBorder != null) {
        Brush.linearGradient(
            listOf(accentBorder.copy(alpha = 0.75f), glassStyle.neumorphicBorderLight, accentBorder.copy(alpha = 0.35f)),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    } else if (isPressed) {
        Brush.linearGradient(
            listOf(glassStyle.neumorphicBorderDark, glassStyle.neumorphicBorderLight.copy(alpha = 0.45f)),
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

    val surfaceBrush = if (glassStyle.isNeumorphic) {
        val topTone = if (isPressed) glassStyle.cardSubtleBg else surfaceBase
        val bottomTone = if (isPressed) surfaceBase else glassStyle.neumorphicBase
        Brush.linearGradient(
            colors = listOf(topTone, bottomTone),
            start = Offset(0f, 0f),
            end = Offset.Infinite
        )
    } else if (isPressed) {
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
                offsetDp = elevationOffset,
                isPressed = isPressed,
                surfaceColor = if (glassStyle.isNeumorphic) surfaceBase else Color.Unspecified,
                blurDp = shadowBlur
            )
            .clip(shape)
            .background(surfaceBrush)
            .border(width = 1.dp, brush = bevelBorderBrush, shape = shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Interactive Neumorphic Button with clean rounded corners, dual-shadow elevation,
 * and smooth spring-damped indented depression on touch.
 * Supports optional [buttonColor] for vibrant solid accent actions (Blue, Mint Green, Red).
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    accentColor: Color? = null,
    buttonColor: Color? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val glassStyle = LocalLiquidGlass.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (enabled && isPressed) 0.955f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.56f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btn_scale"
    )

    val effectiveAccent = buttonColor ?: accentColor ?: glassStyle.accentPrimary

    val buttonBrush = when {
        buttonColor != null -> Brush.linearGradient(
            if (isPressed) {
                listOf(buttonColor.copy(alpha = 0.88f), buttonColor)
            } else {
                listOf(buttonColor, buttonColor.copy(alpha = 0.92f))
            }
        )
        isPressed -> Brush.linearGradient(
            listOf(glassStyle.cardSubtleBg, glassStyle.neumorphicSurface)
        )
        else -> Brush.linearGradient(
            listOf(glassStyle.neumorphicSurface, glassStyle.neumorphicBase)
        )
    }

    val effectiveSurfaceColor = when {
        buttonColor != null -> buttonColor
        glassStyle.isNeumorphic -> glassStyle.neumorphicSurface
        else -> Color.Unspecified
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .neumorphicDepth(
                shape = shape,
                lightColor = if (buttonColor != null) Color.White.copy(alpha = 0.85f) else glassStyle.neumorphicHighlight,
                darkColor = if (buttonColor != null) buttonColor.copy(alpha = 0.40f) else glassStyle.neumorphicShadow,
                offsetDp = if (glassStyle.isNeumorphic) 5.dp else 4.dp,
                isPressed = isPressed,
                surfaceColor = effectiveSurfaceColor,
                blurDp = 10.dp
            )
            .clip(shape)
            .background(buttonBrush)
            .border(
                width = 1.dp,
                brush = when {
                    buttonColor != null -> Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.45f), buttonColor.copy(alpha = 0.85f))
                    )
                    isPressed -> Brush.linearGradient(
                        listOf(effectiveAccent.copy(alpha = 0.8f), glassStyle.neumorphicBorderDark)
                    )
                    else -> Brush.linearGradient(
                        listOf(glassStyle.neumorphicBorderLight, effectiveAccent.copy(alpha = 0.35f))
                    )
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
    sizeDp: Dp = 48.dp
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
                offsetDp = if (glassStyle.isNeumorphic) 4.dp else 3.dp,
                isPressed = isPressed || isHighlighted,
                surfaceColor = if (glassStyle.isNeumorphic) glassStyle.neumorphicSurface else Color.Unspecified,
                blurDp = 8.dp
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
            modifier = Modifier.size((sizeDp.value * 0.48f).dp)
        )
    }
}

/**
 * Neumorphic Segmented Tab/Pill.
 * Unselected: Gently rests on the surface (extruded).
 * Selected: Deep indented (pressed-in) well with accent highlight and crisp typography.
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
            .defaultMinSize(minHeight = 48.dp)
            .neumorphicDepth(
                shape = shape,
                lightColor = if (isSelected) Color.Transparent else glassStyle.neumorphicHighlight.copy(alpha = if (glassStyle.isNeumorphic) 0.95f else 0.5f),
                darkColor = if (isSelected) glassStyle.neumorphicShadow else glassStyle.neumorphicShadow.copy(alpha = if (glassStyle.isNeumorphic) 0.9f else 0.6f),
                offsetDp = 3.dp,
                isPressed = isSelected,
                surfaceColor = if (glassStyle.isNeumorphic && !isSelected) glassStyle.neumorphicSurface else Color.Unspecified,
                blurDp = 6.dp
            )
            .clip(shape)
            .background(
                if (isSelected) {
                    Brush.linearGradient(
                        listOf(
                            glassStyle.cardSubtleBg,
                            effectiveAccent.copy(alpha = if (glassStyle.isNeumorphic) 0.14f else 0.16f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            glassStyle.neumorphicSurface,
                            glassStyle.neumorphicBase
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = if (isSelected) {
                    Brush.linearGradient(listOf(effectiveAccent.copy(alpha = 0.8f), glassStyle.neumorphicBorderDark))
                } else {
                    Brush.linearGradient(listOf(glassStyle.neumorphicBorderLight, glassStyle.neumorphicBorderDark))
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
                    offsetDp = 2.dp,
                    surfaceColor = if (glassStyle.isNeumorphic) {
                        if (checked) effectiveAccent else Color.White
                    } else Color.Unspecified,
                    blurDp = 4.dp
                )
                .clip(CircleShape)
                .background(
                    when {
                        checked -> effectiveAccent
                        glassStyle.isNeumorphic -> Color.White
                        else -> Color(0xFFD1D5DB)
                    }
                )
                .border(0.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
        )
    }
}
