package no.usn.kulturminner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UploadButton(
    text: String,
    borderColor: Color = Color(0xFF4F46A3),
    backgroundColor: Color = Color(0xFFDEDDE6),
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 12.dp,
            vertical = 0.dp
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.FileUpload,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = borderColor
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = borderColor
        )
    }
}