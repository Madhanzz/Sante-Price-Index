package com.example.santepriceindex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*

// --- TRENDS THEME COLORS ---
private val TrendBgStart    = Color(0xFFFDF2F8) // Very light rose
private val TrendBgEnd      = Color(0xFFF5F3FF) // Very light lavender
private val ColorRising     = Color(0xFF10B981) // Emerald Green
private val ColorFalling    = Color(0xFFEF4444) // Soft Red
private val ColorStable     = Color(0xFF6366F1) // Indigo
private val TextMain        = Color(0xFF1E293B)

data class TrendItem(
    val name: String,
    val today: Int,
    val previous: Int
)

@Composable
fun TrendScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val list = remember { mutableStateListOf<TrendItem>() }
    val ref = FirebaseDatabase.getInstance().getReference("prices")

    LaunchedEffect(Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (child in snapshot.children) {
                    val name = child.key ?: ""
                    val today = child.child("mandi").value?.toString()?.toIntOrNull() ?: 0
                    val previous = child.child("previous").value?.toString()?.toIntOrNull() ?: today
                    list.add(TrendItem(name.replace("_", " "), today, previous))
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(TrendBgStart, TrendBgEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- HEADER ---
            Text(
                text = "Market Insights",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = TextMain,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Price movement over the last 24h",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // --- TREND LIST ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(list) { item ->
                    TrendRow(item)
                }
            }
        }
    }
}

@Composable
fun TrendRow(item: TrendItem) {
    // Calculate Trend Logic
    val diff = item.today - item.previous
    val percentChange = if (item.previous != 0) (diff.toDouble() / item.previous * 100) else 0.0

    val (statusText, statusColor, icon) = when {
        item.today > item.previous -> Triple("RISING", ColorRising, Icons.Default.TrendingUp)
        item.today < item.previous -> Triple("FALLING", ColorFalling, Icons.Default.TrendingDown)
        else -> Triple("STABLE", ColorStable, Icons.Default.TrendingFlat)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Price
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = "Current: ₹${item.today}/kg",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Trend Indicator
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${if (diff >= 0) "+" else ""}${String.format("%.1f", percentChange)}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor
                )
            }
        }
    }
}