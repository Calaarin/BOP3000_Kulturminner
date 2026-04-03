package no.usn.kulturminner.ui.mediapanel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaTextScreen(
    onSaveClick: (TextSection) -> Unit,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var columnCount by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Legg til tekst-seksjon") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tittel (valgfri)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Innhold") },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Antall kolonner:")
            Row {
                RadioButton(selected = columnCount == 1, onClick = { columnCount = 1 })
                Text("1 kolonne", modifier = Modifier.padding(top = 12.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = columnCount == 2, onClick = { columnCount = 2 })
                Text("2 kolonner", modifier = Modifier.padding(top = 12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSaveClick(TextSection(title, content, columnCount)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lagre til Mediapanel")
            }
        }
    }
}