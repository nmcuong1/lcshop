package com.example.lcshop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CheckoutScreen(navController: NavHostController, total: Int) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Thanh toán", color = Color.White) }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = Color(0xFF001F5B))
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0đ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Button(
                    onClick = { /* TODO: Handle checkout */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
                ) {
                    Text("Thanh toán", color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
        ) {
            // Địa chỉ giao hàng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("select_address") }
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("aaaaaa/sđt", fontWeight = FontWeight.Bold)
                    Text("TP.HCM")
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sản phẩm trong giỏ hàng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("x1")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Voucher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBox, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voucher", fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Phương thức thanh toán
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Phương thức thanh toán", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thanh toán khi nhận hàng")
                    Switch(
                        checked = false,
                        onCheckedChange = { /* TODO */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chi tiết thanh toán
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Chi tiết thanh toán", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                RowItem("Tổng tiền hàng", "0đ")
                RowItem("Voucher giảm", "-0đ")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                RowItem("Tổng thanh toán", "0đ", bold = true)
            }
        }
    }
}

@Composable
fun RowItem(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}
