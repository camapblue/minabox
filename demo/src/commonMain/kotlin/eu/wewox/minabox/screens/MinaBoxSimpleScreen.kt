package eu.wewox.minabox.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import eu.wewox.minabox.Example
import eu.wewox.minabox.MinaBox
import eu.wewox.minabox.MinaBoxItem
import eu.wewox.minabox.MinaBoxScrollDirection
import eu.wewox.minabox.MinaBoxState
import eu.wewox.minabox.rememberMinaBoxState
import eu.wewox.minabox.ui.components.TopBar
import kotlin.math.abs

/**
 * Simple Mina Box layout example.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinaBoxSimpleScreen() {
    Scaffold(
        topBar = { TopBar(Example.MinaBoxSimple.label) }
    ) { padding ->
        val itemSizePx = with(LocalDensity.current) { ItemSize.toSize() }

        val state = rememberMinaBoxState()
        var lastTranslate: MinaBoxState.Translate? = null
        var scrollingDirection by remember { mutableStateOf(MinaBoxScrollDirection.BOTH) }
        LaunchedEffect(state) {
            snapshotFlow { state.translate }
                .collect { translate ->
                    translate?.let { current ->
                        lastTranslate?.let { last ->
                            val offsetX = abs(current.x - last.x)
                            val offsetY = abs(current.y - last.y)
//                            println("Offset >> ($offsetX, $offsetY)")
                            if (scrollingDirection == MinaBoxScrollDirection.BOTH) {
                                if (offsetX > offsetY && offsetX - offsetY > 5.0) {
                                    scrollingDirection = MinaBoxScrollDirection.HORIZONTAL
                                } else if (offsetX < offsetY && offsetY - offsetX > 5.0) {
                                    scrollingDirection = MinaBoxScrollDirection.VERTICAL
                                }
                            } else if (offsetX < 1.0 && offsetY < 1.0) {
                                scrollingDirection = MinaBoxScrollDirection.BOTH
                            }
                            lastTranslate = current
                        } ?: run {
                            lastTranslate = current
                        }
                    }
                }
        }
        println("Scrolling NOW >> $scrollingDirection")
        MinaBox(
            modifier = Modifier.padding(padding),
            scrollDirection = scrollingDirection,
            state = state
            ) {
            items(
                count = ColumnsCount * RowsCount,
                layoutInfo = {
                    val column = it % ColumnsCount
                    val row = it / ColumnsCount
                    MinaBoxItem(
                        x = itemSizePx.width * column,
                        y = itemSizePx.height * row,
                        width = itemSizePx.width,
                        height = itemSizePx.height,
                    )
                }
            ) { index ->
                Text(
                    text = "Index #$index",
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                )
            }
        }
    }
}

private const val ColumnsCount = 50
private const val RowsCount = 50
private val ItemSize = DpSize(144.dp, 48.dp)
