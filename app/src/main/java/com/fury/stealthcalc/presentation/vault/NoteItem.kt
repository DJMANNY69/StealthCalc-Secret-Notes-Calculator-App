package com.fury.stealthcalc.presentation.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fury.stealthcalc.data.Note

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit // Kept for compatibility, even if unused in drag-delete
) {
    // 1. Calculate Contrast
    // Convert the Int color to a Compose Color object
    val backgroundColor = Color(note.color)

    // Check luminance: < 0.5 means it's dark, so we need White text.
    val isDark = backgroundColor.luminance() < 0.5f
    val textColor = if (isDark) Color.White else Color.Black
    val contentColor = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor, // Applied here
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontSize = 14.sp,
                color = contentColor, // Applied here
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}