package com.example.santepriceindex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- LUXURY GREEN FAMILY THEME ---
private val MidnightForest  = Color(0xFF022C22) // Extremely dark green background
private val CharcoalGreen   = Color(0xFF064E3B) // Rich, deep green for cards
private val LimeNeon        = Color(0xFFBEF264) // Electric Lime for highlights
private val EmeraldText     = Color(0xFF10B981) // Soft Emerald for sub-labels
private val PureWhite       = Color(0xFFFFFFFF)
private val MutedGreen      = Color(0xFF64748B)

data class FeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
)

@Composable
fun MainScreen(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    val scrollState = rememberScrollState()

    val features = listOf(
        FeatureItem("Price Watch", "Live Mandi updates", Icons.Default.BarChart, LimeNeon),
        FeatureItem("Profit Calculator", "Calculate your margins", Icons.Default.Calculate, Color(0xFF38BDF8)),
        FeatureItem("Price Board", "Official digital listings", Icons.Default.Dashboard, Color(0xFFFBBF24)),
        FeatureItem("Trends", "Market forecast analytics", Icons.Default.Analytics, Color(0xFFF472B6))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightForest)
    ) {
        // --- VIBRANT BACKGROUND EMOJIS ---
        Box(modifier = Modifier.fillMaxSize()) {
            val emojiAlpha = 0.35f
            Text("🍅", Modifier.align(Alignment.TopStart).padding(20.dp).alpha(emojiAlpha), fontSize = 50.sp)
            Text("🌽", Modifier.align(Alignment.TopEnd).padding(top = 120.dp, end = 40.dp).alpha(emojiAlpha), fontSize = 45.sp)
            Text("🥦", Modifier.align(Alignment.CenterStart).padding(start = 20.dp).alpha(emojiAlpha), fontSize = 55.sp)
            Text("🥕", Modifier.align(Alignment.CenterEnd).padding(end = 30.dp).alpha(emojiAlpha), fontSize = 50.sp)
            Text("🧅", Modifier.align(Alignment.BottomStart).padding(bottom = 150.dp, start = 50.dp).alpha(emojiAlpha), fontSize = 40.sp)
            Text("🌾", Modifier.align(Alignment.BottomEnd).padding(30.dp).alpha(emojiAlpha), fontSize = 60.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            // --- PREMIUM LOGO ---
            Surface(
                modifier = Modifier.size(86.dp),
                shape = RoundedCornerShape(26.dp),
                color = CharcoalGreen,
                shadowElevation = 20.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(LimeNeon, CharcoalGreen))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = MidnightForest,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Sante Price Index",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = PureWhite,
                letterSpacing = (-1).sp
            )
            Text(
                text = "MARKET INTELLIGENCE HUB",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldText,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(32.dp))

            // --- STATS CARDS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MarketStatPill("247", "Active Items", Modifier.weight(1f))
                MarketStatPill("+12%", "Avg Margin", Modifier.weight(1f))
            }

            Spacer(Modifier.height(32.dp))

            // --- FEATURE CARDS ---
            features.forEach { item ->
                PremiumFeatureCard(
                    item = item,
                    onClick = {
                        val route = when (item.title) {
                            "Price Watch" -> "pricewatch"
                            "Profit Calculator" -> "calculator"
                            "Price Board" -> "priceboard"
                            else -> "trends"
                        }
                        onNavigate(route)
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun MarketStatPill(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = CharcoalGreen,
        shadowElevation = 10.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldText.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = LimeNeon)
            Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldText)
        }
    }
}

@Composable
fun PremiumFeatureCard(item: FeatureItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = CharcoalGreen,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, PureWhite.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Square with Neon Glow
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(item.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = EmeraldText
                )
            }

            // Arrow with accent color
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = item.accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}