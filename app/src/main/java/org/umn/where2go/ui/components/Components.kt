package org.umn.where2go.ui.components

import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.* 
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.umn.where2go.data.Vibe
import org.umn.where2go.ui.theme.VibeLivelyColor
import org.umn.where2go.ui.theme.VibeQuietColor
import org.umn.where2go.ui.theme.VibeStudyColor

fun Vibe.color(): Color = when (this) {
    Vibe.QUIET -> VibeQuietColor
    Vibe.STUDY -> VibeStudyColor
    Vibe.LIVELY -> VibeLivelyColor
}

@Composable
fun VibeBadge(vibe: Vibe) {
    Surface(color = vibe.color().copy(alpha = 0.16f), shape = MaterialTheme.shapes.small) {
        Text(vibe.label, color = vibe.color(), style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
fun Pill(text: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
        Text(text, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

/** Text field standar: isError + supportingText + keyboard type */
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    minLines: Int = 1,
    helper: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) else if (helper !=null) Text(helper) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth()
    )
}