package com.example.santepriceindex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

// --- BOARD THEME COLORS ---
private val BoardDarkBg      = Color(0xFF121212)
private val BoardSurface     = Color(0xFF1E1E1E)
private val AccentGold       = Color(0xFFFFD700)
private val SoftWhite        = Color(0xFFE0E0E0)
private val DividerColor     = Color(0xFF333333)

data class BoardItem(
    val name: String,
    val price: Int
)

@Composable
fun PriceBoardScreen(onBack: () -> Unit) {

    BackHandler { onBack() }

    val itemList = remember { mutableStateListOf<BoardItem>() }
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("prices")

    LaunchedEffect(Unit) {
        ref.get().addOnSuccessListener { snapshot ->
            itemList.clear()
            for (child in snapshot.children) {
                val name = child.key ?: ""
                val selling = child.child("selling").value
                    ?.toString()
                    ?.toDoubleOrNull()
                    ?.toInt() ?: 0
                itemList.add(BoardItem(name.replace("_", " ").uppercase(), selling))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoardDarkBg)
    ) {
        // --- DECORATIVE BACKGROUND EMOJIS (FIXED ALPHA) ---
        // --- DECORATIVE BACKGROUND EMOJIS (MODIFIED FOR VISIBILITY) ---
        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            // Top Left - Tomato
            Text("🍅",
                modifier = Modifier.align(Alignment.TopStart).alpha(0.3f), // Increased to 30%
                fontSize = 44.sp
            )
            // Top Right - Broccoli
            Text("🥦",
                modifier = Modifier.align(Alignment.TopEnd).alpha(0.25f),
                fontSize = 34.sp
            )
            // Bottom Left - Carrot
            Text("🥕",
                modifier = Modifier.align(Alignment.BottomStart).alpha(0.3f),
                fontSize = 38.sp
            )
            // Bottom Right - Onion
            Text("🧅",
                modifier = Modifier.align(Alignment.BottomEnd).alpha(0.3f),
                fontSize = 48.sp
            )
            // Center - Large Corn
            Text("🌽",
                modifier = Modifier.align(Alignment.Center).alpha(0.15f),
                fontSize = 60.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                color = AccentGold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "OFFICIAL PRICE BOARD",
                    color = AccentGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = "Today's Market",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, DividerColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BoardSurface.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("COMMODITY", color = AccentGold.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("SELLING PRICE", color = AccentGold.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = DividerColor, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(itemList) { item ->
                            BoardRow(item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Last updated: Just now",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BoardRow(item: BoardItem) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                color = SoftWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹",
                    color = AccentGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "${item.price}",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}