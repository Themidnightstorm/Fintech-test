package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LessonEntity
import com.example.ui.components.ThinGoldDivider
import com.example.ui.theme.DarkOakBrown
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailModal(
    lesson: LessonEntity?,
    onDismiss: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    if (lesson == null) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianBlack,
        dragHandle = null,
        modifier = Modifier.testTag("lesson_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = "Lesson",
                        tint = SovereignGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "LESSON ${lesson.lessonNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SovereignGold,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_lesson_detail_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SovereignGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            ThinGoldDivider(alpha = 0.35f)
            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Serif Headline in Gold (#C9A84C)
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = SovereignGold,
                        lineHeight = 30.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = lesson.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = LustrousGold.copy(alpha = 0.9f),
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Core Sovereign Principle Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = DeepOakBrown,
                    border = BorderStroke(1.dp, SovereignGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "KEY TAKEAWAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SovereignGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.principle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                color = ParchmentCream,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Cream (#F5E6D3) Body Text in Sans-serif
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        color = ParchmentCream,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Executive Action Item
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkOakBrown,
                    border = BorderStroke(1.dp, RichOakBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "WHAT TO DO TODAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LustrousGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.actionItem,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ParchmentCream,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Completion Toggle Button at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (lesson.isCompleted) Brush.linearGradient(listOf(DeepOakBrown, DeepOakBrown))
                        else Brush.horizontalGradient(listOf(SovereignGold, LustrousGold))
                    )
                    .border(
                        1.dp,
                        if (lesson.isCompleted) SovereignGold else RichOakBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onToggleCompleted(!lesson.isCompleted)
                    }
                    .padding(vertical = 14.dp)
                    .testTag("toggle_lesson_mastered_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = "Status",
                        tint = if (lesson.isCompleted) SovereignGold else ObsidianBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (lesson.isCompleted) "LESSON COMPLETED ✓" else "MARK AS COMPLETED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (lesson.isCompleted) SovereignGold else ObsidianBlack,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
