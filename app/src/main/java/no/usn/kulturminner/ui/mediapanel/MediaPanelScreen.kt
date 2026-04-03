package no.usn.kulturminner.ui.mediapanel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaPanelScreen(uiState: MediaPanelUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(uiState.sections) { section ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                TextSectionItem(
                    section = section,
                    modifier = Modifier.padding(16.dp) // Her brukes paddingen inne i kortet
                )
            }
        }
    }
}

@Composable
fun TextSectionItem(section: TextSection, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!section.title.isNullOrBlank()) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (!section.content.isNullOrBlank()) {
            if (section.columnCount == 2) {
                TwoColumnText(text = section.content)
            } else {
                Text(
                    text = section.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TwoColumnText(text: String) {
    val splitIndex = run {
        val mid = text.length / 2
        val spaceAfter = text.indexOf(' ', mid)
        val spaceBefore = text.lastIndexOf(' ', mid)
        when {
            spaceAfter == -1 && spaceBefore == -1 -> mid
            spaceAfter == -1 -> spaceBefore
            spaceBefore == -1 -> spaceAfter
            else -> if ((spaceAfter - mid) <= (mid - spaceBefore)) spaceAfter else spaceBefore
        }
    }

    val leftText = text.substring(0, splitIndex).trim()
    val rightText = text.substring(splitIndex).trim()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = leftText, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(text = rightText, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
    }
}