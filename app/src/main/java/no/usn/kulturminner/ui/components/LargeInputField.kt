package no.usn.kulturminner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LargeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(154.dp),
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF9E9E9E)
            )
        },
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        maxLines = 5,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            // Ingen border:
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            // Lys grå border (bytt ut Transparent med denne for å prøve):
            // unfocusedBorderColor = Color(0xFFE0E0E0),
            // focusedBorderColor = Color(0xFFBDBDBD)
        )
    )
}