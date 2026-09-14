package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LessonEntity
import com.example.ui.theme.BurgundyGlow
import com.example.ui.theme.DeepOakBrown
import com.example.ui.theme.LustrousGold
import com.example.ui.theme.MutedCream
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.RichOakBorder
import com.example.ui.theme.SovereignGold

@Composable
fun DailyLessonCard(
    lesson: LessonEntity?,
    isPremiumUser: Boolean,
    onReadLesson: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (lesson == null) return

    val isLocked = lesson.isPremium && !isPremiumUser

    WoodCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onReadLesson() }
            .testTag("daily_finance_lesson_card"),
        goldAccentBorder = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Lesson Number + Tier status
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
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "LESSON ${lesson.lessonNumber} OF 30",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            color = SovereignGold
                        )
                    )
                }

                if (isLocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepOakBrown)
                            .border(1.dp, RichOakBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = SovereignGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "PRO ONLY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignGold
                                )
                            )
                        }
                    }
                } else if (lesson.isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = SovereignGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SovereignGold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Serif Headline in Gold (#C9A84C)
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = SovereignGold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lesson.subtitle,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 13.sp,
                    color = SovereignGold.copy(alpha = 0.85f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Cream (#F5E6D3) Body Text in Sans-Serif
            Text(
                text = lesson.principle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.SansSerif,
                    color = ParchmentCream,
                    lineHeight = 20.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))
            ThinGoldDivider(alpha = 0.25f)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Completion toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(enabled = !isLocked) {
                            onToggleCompleted(!lesson.isCompleted)
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (lesson.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = "Mark status",
                        tint = if (lesson.isCompleted) SovereignGold else MutedCream,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (lesson.isCompleted) "Completed" else "Mark Complete",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            color = if (lesson.isCompleted) SovereignGold else ParchmentCream,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                val interaction = remember { MutableInteractionSource() }
                val isPressed by interaction.collectIsPressedAsState()
                val btnBorder by animateColorAsState(
                    targetValue = if (isPressed) BurgundyGlow else SovereignGold.copy(alpha = 0.6f),
                    animationSpec = tween(150),
                    label = "lesson_btn_glow"
                )

                // Read full lesson button with burgundy tap glow
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = if (isPressed) 6.dp else 2.dp,
                            shape = RoundedCornerShape(6.dp),
                            ambientColor = if (isPressed) BurgundyGlow else ObsidianBlack,
                            spotColor = if (isPressed) BurgundyGlow else ObsidianBlack
                        )
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFB58E35),
                                    SovereignGold,
                                    Color(0xFFE5CC82),
                                    SovereignGold,
                                    Color(0xFFB58E35)
                                )
                            )
                        )
                        .border(
                            width = if (isPressed) 2.dp else 1.dp,
                            color = btnBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = onReadLesson
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("read_daily_lesson_button")
                ) {
                    Text(
                        text = if (isLocked) "UNLOCK LESSON" else "READ LESSON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
