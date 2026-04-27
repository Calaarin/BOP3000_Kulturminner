package no.usn.kulturminner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SmallInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF9E9E9E) // Grå placeholder tekst burde se bedre ut tror jeg
            )
        },
        textStyle = LocalTextStyle.current.copy(color = Color.Black),  // svart input-tekst
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            // Går sannsynligvis for ingen border:
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            // Alternativ som kan vurderes: Lys grå border
            // unfocusedBorderColor = Color(0xFFE0E0E0),
            // focusedBorderColor = Color(0xFFBDBDBD)
        )
    )
}