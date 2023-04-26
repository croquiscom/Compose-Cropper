package com.smarttoolfactory.cropper.draw

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.smarttoolfactory.cropper.model.*
import com.smarttoolfactory.cropper.util.drawGrid
import com.smarttoolfactory.cropper.util.drawWithLayer
import com.smarttoolfactory.cropper.util.scaleAndTranslatePath

/**
 * Draw overlay composed of 9 rectangles. When [drawHandles]
 * is set draw handles for changing drawing rectangle
 */
@Composable
internal fun DrawingOverlay(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cropOutline: CropOutline,
    drawGrid: Boolean,
    transparentColor:Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Dp,
    handleStrokeWidth: Dp,
    drawHandles: Boolean,
    handleSize: Float
) {
    val density = LocalDensity.current
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current

    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }
    val handleStrokeWidthPx = LocalDensity.current.run { handleStrokeWidth.toPx() }

    val pathHandles = remember {
        Path()
    }

    val cornerRadius = if (cropOutline is RoundedCornerCropShape) {
        cropOutline.cornerRadius
    } else {
        CornerRadiusProperties(0f)
    }

    when (cropOutline) {
        is CropShape -> {

            val outline = remember(rect, cropOutline) {
                cropOutline.shape.createOutline(rect.size, layoutDirection, density)
            }

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                cornerRadius = cornerRadius,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                handleStrokeWidth = handleStrokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                outline = outline
            )
        }
        is CropPath -> {
            val path = remember(rect, cropOutline) {
                Path().apply {
                    addPath(cropOutline.path)
                    scaleAndTranslatePath(rect.width, rect.height)
                }
            }

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                cornerRadius = cornerRadius,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                handleStrokeWidth = handleStrokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                path = path
            )
        }
        is CropImageMask -> {
            val imageBitmap = cropOutline.image

            DrawingOverlayImpl(
                modifier = modifier,
                drawOverlay = drawOverlay,
                rect = rect,
                cornerRadius = cornerRadius,
                drawGrid = drawGrid,
                transparentColor = transparentColor,
                overlayColor = overlayColor,
                handleColor = handleColor,
                strokeWidth = strokeWidthPx,
                handleStrokeWidth = handleStrokeWidthPx,
                drawHandles = drawHandles,
                handleSize = handleSize,
                pathHandles = pathHandles,
                image = imageBitmap
            )
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cornerRadius: CornerRadiusProperties,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    handleStrokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    outline: Outline,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            cornerRadius,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            handleStrokeWidth,
            drawHandles,
            handleSize,
            pathHandles
        ) {
            drawCropOutline(outline = outline)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cornerRadius: CornerRadiusProperties,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    handleStrokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    path: Path,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            cornerRadius,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            handleStrokeWidth,
            drawHandles,
            handleSize,
            pathHandles
        ) {
            drawCropPath(path)
        }
    }
}

@Composable
private fun DrawingOverlayImpl(
    modifier: Modifier,
    drawOverlay: Boolean,
    rect: Rect,
    cornerRadius: CornerRadiusProperties,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    handleStrokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    image: ImageBitmap,
) {
    Canvas(modifier = modifier) {
        drawOverlay(
            drawOverlay,
            rect,
            cornerRadius,
            drawGrid,
            transparentColor,
            overlayColor,
            handleColor,
            strokeWidth,
            handleStrokeWidth,
            drawHandles,
            handleSize,
            pathHandles
        ) {
            drawCropImage(rect, image)
        }
    }
}

private fun DrawScope.drawOverlay(
    drawOverlay: Boolean,
    rect: Rect,
    cornerRadius: CornerRadiusProperties,
    drawGrid: Boolean,
    transparentColor: Color,
    overlayColor: Color,
    handleColor: Color,
    strokeWidth: Float,
    handleStrokeWidth: Float,
    drawHandles: Boolean,
    handleSize: Float,
    pathHandles: Path,
    drawBlock: DrawScope.() -> Unit
) {
   drawWithLayer {

        // Destination
        drawRect(transparentColor)

        // Source
        translate(left = rect.left, top = rect.top) {
            drawBlock()
        }

        if (drawGrid) {
            drawGrid(
                rect = rect,
                strokeWidth = strokeWidth,
                color = overlayColor
            )
        }
    }

    if (drawOverlay) {
        drawRoundRect(
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = if (cornerRadius.topStart == 0f) {
                CornerRadius.Zero
            } else {
                CornerRadius(x = cornerRadius.topStart, y = cornerRadius.topStart)
            },
            color = overlayColor,
            style = Stroke(width = strokeWidth)
        )

        if (drawHandles) {
            pathHandles.apply {
                reset()
                updateHandlePath(rect, handleSize, handleStrokeWidth, cornerRadius)
            }

            drawPath(
                path = pathHandles,
                color = handleColor,
                style = Stroke(
                    width = handleStrokeWidth,
//                    cap = StrokeCap.Round,
//                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun DrawScope.drawCropImage(
    rect: Rect,
    imageBitmap: ImageBitmap,
    blendMode: BlendMode = BlendMode.DstOut
) {
    drawImage(
        image = imageBitmap,
        dstSize = IntSize(rect.size.width.toInt(), rect.size.height.toInt()),
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropOutline(
    outline: Outline,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawOutline(
        outline = outline,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun DrawScope.drawCropPath(
    path: Path,
    blendMode: BlendMode = BlendMode.SrcOut
) {
    drawPath(
        path = path,
        color = Color.Transparent,
        blendMode = blendMode
    )
}

private fun Path.updateHandlePath(
    rect: Rect,
    handleSize: Float,
    handleStrokeWidth: Float,
    cornerRadius: CornerRadiusProperties,
) {
    if (rect != Rect.Zero) {
        val thick = handleStrokeWidth / 2

        // Top left lines
        val topLeftRadius = cornerRadius.topStart * 2
        if (topLeftRadius > 0) {
            val cornerRadiusOrigin = cornerRadius.topStart
            moveTo(rect.topLeft.x + thick, rect.topLeft.y + thick + handleSize)
            lineTo(x = rect.topLeft.x + thick, y = rect.topLeft.y + thick + cornerRadiusOrigin)
            arcTo(
                rect = Rect(
                    left = rect.topLeft.x + thick,
                    top = rect.topLeft.y + thick,
                    right = rect.topLeft.x - thick + topLeftRadius,
                    bottom = rect.topLeft.y - thick + topLeftRadius
                ),
                startAngleDegrees = 180.0f,
                sweepAngleDegrees = 90.0f,
                forceMoveTo = false
            )
            moveTo(rect.topLeft.x + cornerRadiusOrigin, rect.topLeft.y + thick)
            lineTo(x = rect.topLeft.x + thick + handleSize, y = rect.topLeft.y + thick)
        } else {
            moveTo(rect.topLeft.x, rect.topLeft.y + handleSize)
            lineTo(rect.topLeft.x, rect.topLeft.y)
            lineTo(rect.topLeft.x + handleSize, rect.topLeft.y)
        }

        // Top right lines
        val topRightRadius = cornerRadius.topEnd * 2
        if (topRightRadius > 0) {
            val cornerRadiusOrigin = cornerRadius.topEnd
            moveTo(rect.topRight.x - thick - handleSize, rect.topRight.y + thick)
            lineTo(x = rect.topRight.x - thick - cornerRadiusOrigin, y = rect.topRight.y + thick)
            arcTo(
                rect = Rect(
                    left = rect.topRight.x + thick - topRightRadius,
                    top = rect.topRight.y + thick,
                    right = rect.topRight.x - thick,
                    bottom = rect.topRight.y - thick + topRightRadius
                ),
                startAngleDegrees = -90.0f,
                sweepAngleDegrees = 90.0f,
                forceMoveTo = false
            )
            moveTo(rect.topRight.x - thick, rect.topRight.y + cornerRadiusOrigin)
            lineTo(x = rect.topRight.x - thick, y = rect.topRight.y + thick + handleSize)
        } else {
            moveTo(rect.topRight.x - handleSize, rect.topRight.y)
            lineTo(rect.topRight.x, rect.topRight.y)
            lineTo(rect.topRight.x, rect.topRight.y + handleSize)
        }

        // Bottom right lines
        val bottomRightRadius = cornerRadius.bottomEnd * 2
        if (bottomRightRadius > 0) {
            val cornerRadiusOrigin = cornerRadius.bottomEnd
            moveTo(rect.bottomRight.x - thick, rect.bottomRight.y - thick - handleSize)
            lineTo(x = rect.bottomRight.x - thick, y = rect.bottomRight.y - thick - cornerRadiusOrigin)
            arcTo(
                rect = Rect(
                    left = rect.bottomRight.x + thick - bottomRightRadius,
                    top = rect.bottomRight.y + thick - bottomRightRadius,
                    right = rect.bottomRight.x - thick,
                    bottom = rect.bottomRight.y - thick
                ),
                startAngleDegrees = 0.0f,
                sweepAngleDegrees = 90.0f,
                forceMoveTo = false
            )
            moveTo(rect.bottomRight.x - cornerRadiusOrigin, rect.bottomRight.y - thick)
            lineTo(x = rect.bottomRight.x - thick - handleSize, y = rect.bottomRight.y - thick)
        } else {
            moveTo(rect.bottomRight.x, rect.bottomRight.y - handleSize)
            lineTo(rect.bottomRight.x, rect.bottomRight.y)
            lineTo(rect.bottomRight.x - handleSize, rect.bottomRight.y)
        }

        // Bottom left lines
        val bottomStartRadius = cornerRadius.bottomStart * 2
        if (bottomStartRadius > 0) {
            val cornerRadiusOrigin = cornerRadius.bottomStart
            moveTo(rect.bottomLeft.x + thick + handleSize, rect.bottomLeft.y - thick)
            lineTo(x = rect.bottomLeft.x + thick + cornerRadiusOrigin, y = rect.bottomLeft.y - thick)
            arcTo(
                rect = Rect(
                    left = rect.bottomLeft.x + thick,
                    top = rect.bottomLeft.y + thick - bottomStartRadius,
                    right = rect.bottomLeft.x - thick + bottomStartRadius,
                    bottom = rect.bottomLeft.y - thick
                ),
                startAngleDegrees = 90.0f,
                sweepAngleDegrees = 90.0f,
                forceMoveTo = false
            )
            moveTo(rect.bottomLeft.x + thick, rect.bottomLeft.y - cornerRadiusOrigin)
            lineTo(x = rect.bottomLeft.x + thick, y = rect.bottomLeft.y - thick - handleSize)
        } else {
            moveTo(rect.bottomLeft.x + handleSize, rect.bottomLeft.y)
            lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
            lineTo(rect.bottomLeft.x, rect.bottomLeft.y - handleSize)
        }
    }
}
