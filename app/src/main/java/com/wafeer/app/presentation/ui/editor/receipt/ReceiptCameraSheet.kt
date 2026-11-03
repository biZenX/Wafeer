@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.wafeer.app.presentation.ui.editor.receipt

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.wafeer.app.R
import com.wafeer.app.domain.model.BudgetPeriod
import com.wafeer.app.domain.model.BudgetSplitMode
import com.wafeer.app.domain.model.BudgetState
import com.wafeer.app.domain.model.SupportedCurrency
import com.wafeer.app.presentation.ui.theme.WafeerTheme
import com.wafeer.app.presentation.ui.theme.component.budget.pill.calculateBudgetMetrics
import com.wafeer.app.presentation.util.combineColors
import com.wafeer.app.presentation.util.font.format.symbolOnlyCurrencyFormat
import com.wafeer.app.presentation.util.harmonizeWithColor
import com.wafeer.app.presentation.util.toPaletteWithTheme
import java.math.BigDecimal
import kotlin.math.roundToInt

private val ShutterAreaHeight = 152.dp
private val SheetCornerRadius = 36.dp

@Composable
fun ReceiptCameraSheet(
    visible: Boolean,
    state: ReceiptCameraUiState,
    startHeightPx: Float,
    budgetState: BudgetState?,
    viewPeriod: BudgetPeriod,
    splitMode: BudgetSplitMode,
    currencyCode: String,
    onClose: () -> Unit,
    onCapture: () -> Unit,
    onPickImage: () -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleTorch: () -> Unit,
    modifier: Modifier = Modifier,
    cameraPreview: @Composable (Modifier) -> Unit,
) {
    if (!visible) return

    val density = LocalDensity.current
    val motion = MaterialTheme.motionScheme
    val heightAnim = remember { Animatable(startHeightPx) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
    }

    BackHandler(onBack = onClose)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sheetHeightPx = constraints.maxHeight.toFloat()
        val cornerRadiusPx = with(density) { SheetCornerRadius.toPx() }

        LaunchedEffect(sheetHeightPx) {
            if (sheetHeightPx > 0f) {
                heightAnim.animateTo(sheetHeightPx, motion.defaultSpatialSpec())
            }
        }

        val currentHeightPx = heightAnim.value.coerceAtMost(sheetHeightPx)
        val progress = if (sheetHeightPx > startHeightPx) {
            ((currentHeightPx - startHeightPx) / (sheetHeightPx - startHeightPx)).coerceIn(0f, 1f)
        } else {
            1f
        }
        val topCornerRadius = lerp(cornerRadiusPx, 0f, progress)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, incomingConstraints ->
                    val placeable = measurable.measure(
                        incomingConstraints.copy(
                            minHeight = currentHeightPx.roundToInt(),
                            maxHeight = currentHeightPx.roundToInt(),
                        ),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, (sheetHeightPx - currentHeightPx).roundToInt())
                    }
                }
                .clip(
                    RoundedCornerShape(
                        topStart = topCornerRadius.dp,
                        topEnd = topCornerRadius.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp,
                    ),
                )
                .background(Color.Black),
        ) {
            cameraPreview(Modifier.fillMaxSize())

            FramingSquare(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = ShutterAreaHeight)
                    .graphicsLayer { alpha = alphaAnim.value },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer { alpha = alphaAnim.value },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SheetControlButton(
                    icon = Icons.Rounded.Close,
                    description = stringResource(R.string.close),
                    onClick = onClose,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { alpha = alphaAnim.value }
                    .background(Color.Black.copy(alpha = 0.45f))
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                DetectionPill(
                    state = state,
                    budgetState = budgetState,
                    viewPeriod = viewPeriod,
                    splitMode = splitMode,
                    currencyCode = currencyCode,
                    modifier = Modifier.align(Alignment.TopCenter),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SheetControlButton(
                        icon = Icons.Rounded.PhotoLibrary,
                        description = stringResource(R.string.receipt_camera_pick_image),
                        onClick = onPickImage,
                    )

                    ShutterButton(isCapturing = state.isCapturing, onCapture = onCapture)

                    SheetControlButton(
                        icon = Icons.Rounded.Cameraswitch,
                        description = stringResource(R.string.receipt_camera_switch),
                        enabled = state.canSwitchCamera,
                        onClick = onSwitchCamera,
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetControlButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f)),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.38f),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun DetectionPill(
    state: ReceiptCameraUiState,
    budgetState: BudgetState?,
    viewPeriod: BudgetPeriod,
    splitMode: BudgetSplitMode,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    val detectedTotal = state.detected?.total
    val colors = detectionPillColors(
        detectedTotal = detectedTotal,
        failed = state.captureFailed,
        budgetState = budgetState,
        viewPeriod = viewPeriod,
        splitMode = splitMode,
    )
    val container by animateColorAsState(colors.first, tween(220), label = "receiptPillContainer")
    val content by animateColorAsState(colors.second, tween(220), label = "receiptPillContent")
    val currencyFormat = remember(currencyCode) {
        symbolOnlyCurrencyFormat(
            currencyCode,
            minimumFractionDigits = SupportedCurrency.findByCode(currencyCode)?.defaultFractionDigits ?: 2,
        )
    }

    val label = when {
        state.captureFailed -> stringResource(R.string.receipt_camera_retry)
        detectedTotal != null -> stringResource(R.string.receipt_camera_detected, currencyFormat.format(detectedTotal))
        else -> stringResource(R.string.receipt_camera_point_at_total)
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 20.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = label,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
            label = "receiptPillLabel",
        ) { text ->
            Text(
                text = text,
                color = content,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun detectionPillColors(
    detectedTotal: BigDecimal?,
    failed: Boolean,
    budgetState: BudgetState?,
    viewPeriod: BudgetPeriod,
    splitMode: BudgetSplitMode,
): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    if (failed) return scheme.errorContainer to scheme.onErrorContainer
    if (detectedTotal == null) return scheme.surfaceContainerHighest to scheme.onSurface
    if (budgetState == null) return scheme.primaryContainer to scheme.onPrimaryContainer

    val isDark = isSystemInDarkTheme()
    val status = WafeerTheme.budgetStatus
    val primary = scheme.primary
    return remember(budgetState, viewPeriod, splitMode, detectedTotal, status, primary, isDark) {
        val progress = calculateBudgetMetrics(budgetState, viewPeriod, splitMode, detectedTotal).spendProgress
        val statusColor = combineColors(listOf(status.good, status.notGood, status.bad), progress)
        val palette = toPaletteWithTheme(harmonizeWithColor(statusColor, primary), isDark)
        palette.container to palette.onContainer
    }
}

@Composable
private fun ShutterButton(isCapturing: Boolean, onCapture: () -> Unit) {
    val description = stringResource(R.string.receipt_camera_capture)
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White.copy(alpha = 0.35f), CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(Color(0xFFDADADA))
            .clickable(enabled = !isCapturing, onClick = onCapture)
            .semantics {
                role = Role.Button
                contentDescription = description
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isCapturing) {
            LoadingIndicator(modifier = Modifier.size(40.dp), color = Color(0xFF1B1B1B))
        }
    }
}

@Composable
private fun FramingSquare(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val side = minOf(size.width, size.height) * RECEIPT_FRAME_FRACTION
        val left = center.x - side / 2f
        val top = center.y - side / 2f
        val arm = FrameArmLength.toPx()
        val radius = FrameCornerRadius.toPx()
        val corner = Path().apply {
            moveTo(left, top + arm)
            lineTo(left, top + radius)
            arcTo(Rect(left, top, left + radius * 2, top + radius * 2), 180f, 90f, false)
            lineTo(left + arm, top)
        }
        val stroke = Stroke(width = FrameStrokeWidth.toPx(), cap = StrokeCap.Round)
        listOf(1f to 1f, -1f to 1f, 1f to -1f, -1f to -1f).forEach { (sx, sy) ->
            scale(scaleX = sx, scaleY = sy, pivot = center) {
                drawPath(corner, Color.White, style = stroke)
            }
        }
    }
}

private val FrameArmLength = 36.dp
private val FrameCornerRadius = 20.dp
private val FrameStrokeWidth = 4.dp
