package com.christianhernandez.quoteflow.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.christianhernandez.quoteflow.util.SwipeDirection
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwiped: (SwipeDirection) -> Unit,
    onDoubleTap: (() -> Unit)? = null,
    swipeThresholdDp: Float = 100f,
    content: @Composable (offsetX: Float, offsetY: Float) -> Unit,
) {
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { swipeThresholdDp.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer {
                rotationZ = offsetX.value / 40f
                alpha = 1f - (abs(offsetX.value) / (swipeThresholdPx * 3f)).coerceIn(0f, 0.5f)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap?.invoke() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragX = 0f
                        dragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                        scope.launch {
                            offsetX.snapTo(dragX)
                            offsetY.snapTo(dragY)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val absX = abs(dragX)
                            val absY = abs(dragY)

                            when {
                                absX > swipeThresholdPx && absX > absY -> {
                                    // Horizontal swipe passed threshold
                                    val targetX = if (dragX > 0) swipeThresholdPx * 4f else -swipeThresholdPx * 4f
                                    offsetX.animateTo(targetX, tween(300))
                                    val direction = if (dragX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                                    onSwiped(direction)
                                    // Reset for next card
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                                absY > swipeThresholdPx && absY > absX -> {
                                    // Vertical swipe passed threshold
                                    val targetY = if (dragY > 0) swipeThresholdPx * 4f else -swipeThresholdPx * 4f
                                    offsetY.animateTo(targetY, tween(300))
                                    val direction = if (dragY > 0) SwipeDirection.DOWN else SwipeDirection.UP
                                    onSwiped(direction)
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                }
                                else -> {
                                    // Spring back
                                    launch { offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
                                    launch { offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            launch { offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
                            launch { offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium)) }
                        }
                    }
                )
            }
    ) {
        content(offsetX.value, offsetY.value)
    }
}
