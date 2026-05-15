package chat.nami.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chat.nami.design.NamiTheme

@Composable
@Preview
private fun DrawerContentPreview() {
    NamiTheme {
        DrawerContent(
            userName = "Ömer Karaca",
            recents = mapOf(
                "id-0" to "Android Jetpack Compose tips",
                "id-1" to "Fix navigation bug",
                "id-2" to "Material 3 drawer design",
                "id-3" to "Hello World!"
            )
        )
    }
}
