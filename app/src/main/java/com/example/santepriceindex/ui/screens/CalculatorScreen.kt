package com.example.santepriceindex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase

// --- CALCULATOR THEME COLORS ---
private val CalcDarkBg      = Color(0xFF0F172A)
private val CalcCardBg      = Color(0xFF1E293B)
private val AccentBlue      = Color(0xFF38BDF8)
private val AccentCyan      = Color(0xFF22D3EE)
private val TextPrimary     = Color(0xFFF8FAFC)
private val TextSecondary   = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(onBack: () -> Unit) {

    BackHandler { onBack() }

    val items = remember { mutableStateListOf<String>() }
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }

    var mandiPrice by remember { mutableStateOf("") }
    var transportCost by remember { mutableStateOf("") }
    var profitPercent by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("prices")

    LaunchedEffect(Unit) {
        ref.get().addOnSuccessListener { snapshot ->
            items.clear()
            for (child in snapshot.children) {
                child.key?.let { items.add(it) }
            }
            if (items.isNotEmpty() && selectedItem.isEmpty()) {
                selectedItem = items[0]
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CalcDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- HEADER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = AccentBlue)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Margin Tool",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Calculate smart selling prices",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CalcCardBg),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("VEGETABLE TYPE", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedItem.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CalcCardBg)
                        ) {
                            items.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.uppercase(), color = TextPrimary) },
                                    onClick = {
                                        selectedItem = item
                                        expanded = false
                                        result = ""
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    CustomCalcField(
                        label = "Mandi Price (₹/kg)",
                        value = mandiPrice,
                        onValueChange = { mandiPrice = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomCalcField(
                        label = "Transport Cost (Total ₹)",
                        value = transportCost,
                        onValueChange = { transportCost = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomCalcField(
                        label = "Target Profit (%)",
                        value = profitPercent,
                        onValueChange = { profitPercent = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val mandi = mandiPrice.toDoubleOrNull() ?: 0.0
                            val transport = transportCost.toDoubleOrNull() ?: 0.0
                            val profit = profitPercent.toDoubleOrNull() ?: 0.0

                            val totalCost = mandi + transport
                            val sellingPrice = totalCost + (totalCost * profit / 100)

                            result = "₹${"%.2f".format(sellingPrice)}"

                            val data = mapOf("mandi" to mandi, "selling" to sellingPrice)
                            if (selectedItem.isNotEmpty()) {
                                ref.child(selectedItem).updateChildren(data)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Analyze Margins", fontWeight = FontWeight.Bold, color = CalcDarkBg)
                    }
                }
            }

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentCyan.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SUGGESTED SELLING PRICE", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
                            Text(result, fontSize = 34.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun CustomCalcField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label.uppercase(), fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                focusedContainerColor = CalcDarkBg.copy(alpha = 0.5f),
                unfocusedContainerColor = CalcDarkBg.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}