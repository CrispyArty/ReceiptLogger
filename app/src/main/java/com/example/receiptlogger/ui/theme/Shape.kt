package com.example.receiptlogger.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.ceil


class WavyShape(
    private val period: Dp,
    private val amplitude: Dp,
) : CornerBasedShape(
    topStart = CornerSize(period),
    topEnd = CornerSize(amplitude),
    bottomEnd = CornerSize(0),
    bottomStart = CornerSize(0)
) {
    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): CornerBasedShape {

        return WavyShape(period, amplitude)
    }

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ) = Outline.Generic(Path().apply {
        val wavyPath = Path().apply {
            val halfPeriod = topStart / 2
            val amplitude = topEnd

            moveTo(x = -halfPeriod / 2, y = amplitude)

            repeat(ceil(size.width / halfPeriod + 1).toInt()) { i ->
                val direction = if (i and 1 == 0) -1 else 1

                relativeQuadraticTo(
                    dx1 = halfPeriod / 2,
                    dy1 = 2 * amplitude * direction,
                    dx2 = halfPeriod,
                    dy2 = 0f
                )
            }
            lineTo(size.width + halfPeriod, size.height - amplitude)

            repeat(ceil(size.width / halfPeriod + 1).toInt()) { i ->
                val direction = if (i and 1 == 0) -1 else 1

                relativeQuadraticTo(
                    dx1 = -halfPeriod / 2,
                    dy1 = 2 * amplitude * direction,
                    dx2 = -halfPeriod,
                    dy2 = 0f
                )
            }
        }
        val boundsPath = Path().apply {
            addRect(Rect(offset = Offset.Zero, size = size))
        }
        op(wavyPath, boundsPath, PathOperation.Intersect)
    })
}

val Shapes = Shapes(
    extraSmall = WavyShape(period = 15.dp, amplitude = 2.dp),
    small = WavyShape(period = 15.dp, amplitude = 2.dp),
//    small = RoundedPolygon.pillStar(),

    medium = WavyShape(period = 15.dp, amplitude = 2.dp),
    large = WavyShape(period = 15.dp, amplitude = 2.dp),
    extraLarge = WavyShape(period = 15.dp, amplitude = 2.dp),
)

//val Shapes = Shapes(
//    extraSmall = CutCornerShape(
//        topStart = 12.dp,
//        topEnd = 2.dp,
//        bottomStart = 2.dp,
//        bottomEnd = 2.dp
//    ),
//    small = CutCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
////    small = RoundedPolygon.pillStar(),
//
//    medium = CutCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
//    large = CutCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
//    extraLarge = CutCornerShape(
//        topStart = 16.dp,
//        topEnd = 4.dp,
//        bottomStart = 4.dp,
//        bottomEnd = 4.dp
//    ),
//
//)
