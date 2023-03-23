package com.smarttoolfactory.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class CornerRadiusProperties(
    val topStart: Float = 20f,
    val topEnd: Float = 20f,
    val bottomStart: Float = 20f,
    val bottomEnd: Float = 20f
) {
    constructor(radius: Float) : this(topStart = radius, topEnd = radius, bottomStart = radius, bottomEnd = radius)
}

@Immutable
data class PolygonProperties(
    val sides: Int = 6,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero
)

@Immutable
data class OvalProperties(
    val startAngle: Float = 0f,
    val sweepAngle: Float = 360f,
    val offset: Offset = Offset.Zero
)
