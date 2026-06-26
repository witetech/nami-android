package chat.nami.accessibility.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Sliders & progress (ranged values).
 *
 * A custom-drawn volume bar is just a colored Box: TalkBack sees a node with no
 * value and no way to change it. progressBarRangeInfo exposes the value and its
 * range, stateDescription speaks it as a percentage, and setProgress lets
 * TalkBack and Switch Access move it with the volume keys.
 */
@Composable
fun ProgressBad() {
    var volume by remember { mutableFloatStateOf(0.6f) }
    VolumeControl(volume = volume, onVolume = { volume = it }, barModifier = Modifier)
}

@Composable
fun ProgressGood() {
    var volume by remember { mutableFloatStateOf(0.6f) }
    VolumeControl(
        volume = volume,
        onVolume = { volume = it },
        // mergeDescendants makes the track + fill a single focusable node;
        // without it the Box has a child, so TalkBack never focuses it at all.
        barModifier = Modifier.semantics(mergeDescendants = true) {
            // A focusable slider also needs an accessible name.
            // contentDescription = "Volume"
            // Value + range → spoken after the name: "Volume, 60%".
            stateDescription = "${(volume * 100).roundToInt()}%"
            progressBarRangeInfo = ProgressBarRangeInfo(volume, 0f..1f, steps = 9)
            // Volume keys / TalkBack swipe up-down can now adjust it.
            setProgress { target ->
                volume = target.coerceIn(0f, 1f)
                true
            }
        }
    )
}

@Composable
private fun VolumeControl(volume: Float, onVolume: (Float) -> Unit, barModifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Volume", style = MaterialTheme.typography.titleMedium)
        // Custom-drawn track + fill — no built-in semantics.
        Box(
            modifier = barModifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.surfaceVariant)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(volume.coerceIn(0f, 1f))
                    .background(colors.primary)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onVolume((volume - 0.1f).coerceIn(0f, 1f)) }) { Text("-10%") }
            Button(onClick = { onVolume((volume + 0.1f).coerceIn(0f, 1f)) }) { Text("+10%") }
        }
    }
}
