package chat.nami.accessibility.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Forms & validation.
 *
 * Turning a field red is silent to TalkBack: a screen-reader user taps Submit,
 * nothing is spoken, and they have no idea why the form did not advance. error()
 * flags the field as invalid (announced as "invalid entry"), and a liveRegion on
 * the message speaks it the moment validation fails — without stealing focus.
 */
private fun isValidEmail(value: String): Boolean =
    value.contains("@") && value.substringAfterLast("@").contains(".")

@Composable
fun FormValidationBad() {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = emailError != null,
            // Error lives only as red helper text — never announced.
            supportingText = { emailError?.let { Text(it) } }
        )
        Button(
            onClick = {
                emailError = if (isValidEmail(email)) null else "Enter a valid email address"
            }
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun FormValidationGood() {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(
                        it,
                        // Announce the failure the instant it appears.
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                    )
                }
            },
            // Field itself reads as "Email, invalid entry".
            modifier = Modifier.semantics { emailError?.let { error(it) } }
        )
        Button(
            onClick = {
                emailError = if (isValidEmail(email)) null else "Enter a valid email address"
            }
        ) {
            Text("Submit")
        }
    }
}
