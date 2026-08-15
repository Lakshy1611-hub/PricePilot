package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PricePilotBlue
import com.example.ui.theme.PricePilotPurple

@Composable
fun ShoppingTrolleyLoading(
    modifier: Modifier = Modifier,
    progress: Float = 0.62f
) {
    val transition = rememberInfiniteTransition(label = "trolley")
    val bob by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "bob"
    )
    val wheel by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
        label = "wheel"
    )

    Column(modifier = modifier) {
        Canvas(Modifier.fillMaxWidth().height(150.dp)) {
            val ground = size.height - 20f
            val trolleyX = size.width * 0.43f
            val trolleyY = size.height * 0.43f + bob
            val trolleyW = size.width * 0.36f
            val trolleyH = size.height * 0.22f

            // soft motion trail
            drawLine(PricePilotPurple.copy(alpha = 0.10f), Offset(size.width * .12f, ground), Offset(trolleyX - trolleyW * .25f, ground), 10f, StrokeCap.Round)

            // person
            drawCircle(PricePilotPurple, 15f, Offset(trolleyX - trolleyW * .48f, trolleyY - 48f))
            drawLine(PricePilotPurple, Offset(trolleyX - trolleyW * .48f, trolleyY - 32f), Offset(trolleyX - trolleyW * .42f, trolleyY + 2f), 12f, StrokeCap.Round)
            drawLine(PricePilotPurple, Offset(trolleyX - trolleyW * .42f, trolleyY - 10f), Offset(trolleyX - trolleyW * .30f, trolleyY + 20f), 8f, StrokeCap.Round)
            drawLine(PricePilotPurple, Offset(trolleyX - trolleyW * .42f, trolleyY - 8f), Offset(trolleyX - trolleyW * .55f, trolleyY + 18f), 8f, StrokeCap.Round)
            drawLine(PricePilotPurple, Offset(trolleyX - trolleyW * .43f, trolleyY - 27f), Offset(trolleyX - trolleyW * .28f, trolleyY - 4f), 8f, StrokeCap.Round)

            // trolley basket
            drawRoundRect(
                color = PricePilotBlue.copy(alpha = .18f),
                topLeft = Offset(trolleyX - trolleyW * .05f, trolleyY),
                size = Size(trolleyW, trolleyH),
                cornerRadius = CornerRadius(18f, 18f)
            )
            drawRoundRect(
                color = PricePilotBlue,
                topLeft = Offset(trolleyX - trolleyW * .05f, trolleyY),
                size = Size(trolleyW, trolleyH),
                cornerRadius = CornerRadius(18f, 18f),
                style = Stroke(width = 6f)
            )
            drawLine(PricePilotPurple, Offset(trolleyX - trolleyW * .13f, trolleyY - 8f), Offset(trolleyX + trolleyW * .05f, trolleyY + 2f), 7f, StrokeCap.Round)

            // products inside the trolley
            drawRoundRect(PricePilotPurple, Offset(trolleyX + trolleyW * .05f, trolleyY - 28f), Size(34f, 32f), CornerRadius(8f, 8f))
            drawRoundRect(PricePilotBlue, Offset(trolleyX + trolleyW * .23f, trolleyY - 36f), Size(42f, 40f), CornerRadius(8f, 8f))
            drawRoundRect(PricePilotPurple.copy(alpha = .75f), Offset(trolleyX + trolleyW * .39f, trolleyY - 24f), Size(32f, 28f), CornerRadius(7f, 7f))

            // wheels with subtle animated rotation cue
            val wheelOffset = if (wheel < 180f) 1f else -1f
            drawCircle(PricePilotPurple, 10f, Offset(trolleyX + trolleyW * .14f, trolleyY + trolleyH + 10f))
            drawCircle(PricePilotPurple, 10f, Offset(trolleyX + trolleyW * .75f, trolleyY + trolleyH + 10f))
            drawLine(PricePilotBlue, Offset(trolleyX + trolleyW * .14f, trolleyY + trolleyH + 10f), Offset(trolleyX + trolleyW * .14f + wheelOffset * 6f, trolleyY + trolleyH + 10f), 3f, StrokeCap.Round)

            // celebratory dots at the end of the loading path
            if (progress >= .9f) {
                drawCircle(PricePilotPurple, 5f, Offset(size.width * .86f, size.height * .25f))
                drawCircle(PricePilotBlue, 4f, Offset(size.width * .92f, size.height * .38f))
            }
        }
        Text(
            text = when {
                progress < .25f -> "Preparing your cart…"
                progress < .55f -> "Collecting items…"
                progress < .8f -> "Checking the best prices…"
                progress < .95f -> "Almost there… ✨"
                else -> "Deals found! 🎉"
            },
            style = MaterialTheme.typography.labelLarge,
            color = PricePilotPurple
        )
    }
}
