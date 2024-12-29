package com.braiso_22.canvasclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.braiso_22.canvasclock.ui.theme.CanvasClockTheme
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasClockTheme {
                ClockScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ClockScreen(modifier: Modifier = Modifier) {
    var currentTime by remember {
        mutableStateOf(LocalTime.now())
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            currentTime = LocalTime.now()
            println(currentTime)
            delay(1000)
        }
    }

    Scaffold(modifier) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                currentTime.format(
                    DateTimeFormatter.ofPattern("hh:mm:ss a")
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Clock(
                currentTime = currentTime,
                modifier = Modifier
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ClockScreenPreview() {
    CanvasClockTheme {
        ClockScreen()
    }
}


data class ClockStyles(
    val minuteLineLength: Dp = 8.dp,
    val hourLineLength: Dp = 24.dp,
)


@Composable
fun Clock(
    currentTime: LocalTime,
    modifier: Modifier = Modifier,
    style: ClockStyles = ClockStyles(),
) {
    val color = MaterialTheme.colorScheme.onPrimary
    val linesColor = MaterialTheme.colorScheme.primary
    val secondLineColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier.size(300.dp)) {

        drawCircle(
            color = color,
            radius = size.minDimension / 2,
            center = center,
        )

        drawClockLines(
            linesColor = linesColor,
            hourLineLength = style.hourLineLength,
            minuteLineLength = style.minuteLineLength
        )
        drawTickLine(
            currentSecond = currentTime.second,
            lineColor = secondLineColor,
        )
        drawTickLine(
            currentSecond = currentTime.minute,
            lineColor = linesColor,
            width = 10f
        )
        val hourIn12HourFormat = (currentTime.hour % 12f) + (currentTime.minute / 60f)
        val hourToBase60 = (hourIn12HourFormat * 5).roundToInt()

        drawTickLine(
            currentSecond = hourToBase60,
            lineColor = linesColor,
            width = 15f
        )
    }
}

fun DrawScope.drawClockLines(
    linesColor: Color = Color.White,
    hourLineLength: Dp = 24.dp,
    minuteLineLength: Dp = 8.dp,
) {
    val lines = (0..59).toList()
    for (line in lines) {
        val lineLength = if (line % 5 == 0) {
            hourLineLength
        } else {
            minuteLineLength
        }

        val angle = Math.toRadians(line * 6.0)
        val startX = center.x + center.x * cos(angle).toFloat()
        val endX = center.x + (center.x - lineLength.toPx()) * cos(angle).toFloat()

        val startY = center.y + center.y * sin(angle).toFloat()
        val endY = center.y + (size.height / 2 - lineLength.toPx()) * sin(angle).toFloat()

        drawLine(
            color = linesColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2f
        )
    }
}

fun DrawScope.drawTickLine(
    currentSecond: Int,
    lineColor: Color = Color.Red,
    width: Float = 7f,
) {
    val angle = Math.toRadians(currentSecond * 6.0) - Math.toRadians(90.0)

    drawLine(
        color = lineColor,
        start = Offset(
            center.x - (8.dp.toPx()) * cos(angle).toFloat(),
            center.y - (8.dp.toPx()) * sin(angle).toFloat()
        ),
        end = Offset(
            center.x + (center.x - 16.dp.toPx()) * cos(angle).toFloat(),
            center.y + (center.y - 16.dp.toPx()) * sin(angle).toFloat()
        ),
        strokeWidth = width
    )
}


@PreviewLightDark
@Composable
private fun ClockPreview() {
    CanvasClockTheme {
        Clock(LocalTime.now())
    }
}