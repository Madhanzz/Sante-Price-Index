package com.example.santepriceindex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*

// --- THEME COLORS ---
private val MandiGreen      = Color(0xFF2E7D32)
private val MandiGreenLight = Color(0xFFE8F5E9)
private val MandiBg         = Color(0xFFFBFDFA)
private val PriceText       = Color(0xFF1B5E20)
private val CardWhite       = Color(0xFFFFFFFF)

// --- DATA MODEL ---
data class PriceItem(
    val name: String,
    val price: Int
)

// --- MAIN SCREEN ---
@Composable
fun PriceWatchScreen(onBack: () -> Unit) {

    BackHandler { onBack() }

    val priceList = remember { mutableStateListOf<PriceItem>() }
    var isLoading by remember { mutableStateOf(true) }

    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("prices")

    LaunchedEffect(Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                priceList.clear()
                for (child in snapshot.children) {
                    val name = child.key ?: ""
                    val mandi = child.child("mandi").value
                        ?.toString()
                        ?.toDoubleOrNull()
                        ?.toInt() ?: 0

                    priceList.add(
                        PriceItem(
                            name.replace("_", " "),
                            mandi
                        )
                    )
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MandiBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Daily Market",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MandiGreen,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Fresh price updates",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MandiGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌿", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MandiGreen, strokeWidth = 3.dp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(priceList) { item ->
                        PriceRow(item)
                    }
                }
            }
        }
    }
}

// --- ITEM ROW COMPONENT ---
@Composable
fun PriceRow(item: PriceItem) {
    // Vegetable Emoji Logic
    val emoji = when {
        item.name.contains("Tomato", ignoreCase = true) -> "🍅"
        item.name.contains("Potato", ignoreCase = true) -> "🥔"
        item.name.contains("Onion", ignoreCase = true) -> "🧅"
        item.name.contains("Chilli", ignoreCase = true) -> "🌶️"
        item.name.contains("Carrot", ignoreCase = true) -> "🥕"
        item.name.contains("Ginger", ignoreCase = true) -> "🫚"
        item.name.contains("Garlic", ignoreCase = true) -> "🧄"
        item.name.contains("Brinjal", ignoreCase = true) -> "🍆"
        item.name.contains("Lemon", ignoreCase = true) -> "🍋"
        item.name.contains("Ladies", ignoreCase = true) -> "🥒"
        else -> "🥗"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Square
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MandiGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Label Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "Local Market Avg",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Price Details
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${item.price}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = PriceText
                )
                Text(
                    text = "per kg",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MandiGreen
                )
            }
        }
    }
}