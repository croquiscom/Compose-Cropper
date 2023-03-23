package com.smarttoolfactory.composecropper.preferences.frames.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.composecropper.preferences.CropTextField
import com.smarttoolfactory.composecropper.preferences.SliderWithValueSelection
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CornerRadiusProperties
import com.smarttoolfactory.cropper.model.RoundedCornerCropShape
import com.smarttoolfactory.cropper.util.drawOutlineWithBlendModeAndChecker
import kotlin.math.roundToInt

@Composable
internal fun RoundedCornerCropShapeEdit(
    aspectRatio: AspectRatio,
    dstBitmap: ImageBitmap,
    title: String,
    roundedCornerCropShape: RoundedCornerCropShape,
    onChange: (RoundedCornerCropShape) -> Unit
) {

    var newTitle by remember {
        mutableStateOf(title)
    }

    val cornerRadius = remember {
        roundedCornerCropShape.cornerRadius
    }

    var topStart by remember {
        mutableStateOf(
            cornerRadius.topStart
        )
    }

    var topEnd by remember {
        mutableStateOf(
            cornerRadius.topEnd
        )
    }

    var bottomStart by remember {
        mutableStateOf(
            cornerRadius.bottomStart
        )
    }

    var bottomEnd by remember {
        mutableStateOf(
            cornerRadius.bottomEnd
        )
    }

    val shape by remember {
        derivedStateOf {
            RoundedCornerShape(
                topStart = topStart,
                topEnd = topEnd,
                bottomStart = bottomStart,
                bottomEnd = bottomEnd
            )
        }
    }

    onChange(
        roundedCornerCropShape.copy(
            cornerRadius = CornerRadiusProperties(
                topStart = topStart,
                topEnd = topEnd,
                bottomStart = bottomStart,
                bottomEnd = bottomEnd
            ),
            title = newTitle,
            shape = shape
        )
    )

    Column {

        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f)
                .clipToBounds()
                .drawOutlineWithBlendModeAndChecker(
                    aspectRatio,
                    shape,
                    density,
                    dstBitmap
                )
        )

        CropTextField(
            value = newTitle,
            onValueChange = { newTitle = it }
        )

        Spacer(modifier=Modifier.height(10.dp))

        SliderWithValueSelection(
            value = topStart,
            title = "Top Start",
            text = "${(topStart * 10f).roundToInt() / 10f}%",
            onValueChange = { topStart = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = topEnd,
            title = "Top End",
            text = "${(topEnd * 10f).roundToInt() / 10f}%",
            onValueChange = { topEnd = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = bottomStart,
            title = "Bottom Start",
            text = "${(bottomStart * 10f).roundToInt() / 10f}%",
            onValueChange = { bottomStart = it },
            valueRange = 0f..100f
        )
        SliderWithValueSelection(
            value = bottomEnd,
            title = "Bottom End",
            text = "${(bottomEnd * 10f).roundToInt() / 10f}%",
            onValueChange = { bottomEnd = it },
            valueRange = 0f..100f
        )
    }
}
