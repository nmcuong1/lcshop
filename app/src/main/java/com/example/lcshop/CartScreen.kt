package com.example.lcshop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(
                color = if (checked) Color(0xFF001F5B) else Color.White,
                shape = CircleShape
            )
            .border(2.dp, Color(0xFF001F5B), CircleShape)
            .clickable { onCheckedChange(!checked) }
    )
}

@Composable
fun CartScreen(navController: NavHostController) {
    var selectedAll by remember { mutableStateOf(false) }
    var quantities by remember { mutableStateOf(listOf(1, 2, 1)) }
    var selectedItems by remember { mutableStateOf(MutableList(quantities.size) { false }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF001F5B)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LC Shop", color = Color.White, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
        }

        // Select All + Delete
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundCheckbox(
                checked = selectedAll,
                onCheckedChange = {
                    selectedAll = it
                    selectedItems = selectedItems.map { _ -> it }.toMutableList()
                }
            )
            Text("  ALL", modifier = Modifier.weight(1f))
            IconButton(onClick = {
                val newQuantities = mutableListOf<Int>()
                val newSelectedItems = mutableListOf<Boolean>()

                quantities.forEachIndexed { index, qty ->
                    if (!selectedItems[index]) {
                        newQuantities.add(qty)
                        newSelectedItems.add(false)
                    }
                }

                quantities = newQuantities
                selectedItems = newSelectedItems
                selectedAll = selectedItems.all { it }
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }

        // Danh sách sản phẩm
        quantities.forEachIndexed { index, qty ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundCheckbox(
                        checked = selectedItems[index],
                        onCheckedChange = {
                            selectedItems = selectedItems.toMutableList().also { it[index] = !it[index] }
                            selectedAll = selectedItems.all { it }
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.LightGray)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tên sản phẩm ${index + 1}", fontWeight = FontWeight.Bold)
                        Text("Giá: ${(qty * 100000)}đ", color = Color.Red)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                if (quantities[index] > 1) {
                                    quantities = quantities.toMutableList().also { it[index] -= 1 }
                                }
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-", fontSize = 16.sp)
                        }

                        Text(qty.toString(), modifier = Modifier.padding(horizontal = 8.dp))

                        Button(
                            onClick = {
                                quantities = quantities.toMutableList().also { it[index] += 1 }
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("+", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gạch phân cách
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Thanh toán
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val total = quantities.withIndex().sumOf { (i, q) -> if (selectedItems[i]) q * 100000 else 0 }
            Text("${total}đ", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Button(
                onClick = {navController.navigate("checkout/${total}")},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
            ) {
                Text("Thanh toán", color = Color.White)
            }
        }
    }
}
