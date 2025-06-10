package com.example.lcshop
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun SelectAddressScreen(navController: NavHostController) {
    var addressList by remember {
        mutableStateOf(
            mutableListOf(
                "Nguyễn Văn A / 0123456789\n123 Nguyễn Trãi, TP.HCM",
                "Trần Thị B / 0987654321\n456 Lê Lợi, TP.HCM"
            )
        )
    }
    var selectedAddressIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF001F5B)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack()  }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chọn địa chỉ nhận hàng",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Danh sách địa chỉ
        Column(modifier = Modifier.padding(16.dp)) {
            addressList.forEachIndexed { index, address ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(6.dp))
                        .clickable { selectedAddressIndex = index }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                // Round checkbox
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            if (selectedAddressIndex == index) Color(0xFF001F5B) else Color.White,
                                            shape = CircleShape
                                        )
                                        .border(2.dp, Color(0xFF001F5B), CircleShape)
                                        .clickable {
                                            selectedAddressIndex = index
                                        }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                Text(
                                    text = address,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            // Nút sửa + xóa
                            Row {
                                IconButton(onClick = {
                                    // TODO: Hiển thị hộp thoại sửa
                                    println("Sửa địa chỉ $index")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Sửa")
                                }
                                IconButton(onClick = {
                                    addressList = addressList.toMutableList().also { it.removeAt(index) }
                                    if (selectedAddressIndex == index) selectedAddressIndex = -1
                                    else if (selectedAddressIndex > index) selectedAddressIndex--
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Nút thêm địa chỉ
            Button(
                onClick = {
                    addressList = addressList.toMutableList().apply {
                        add("Địa chỉ mới / SĐT\nTP.HCM")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF001F5B))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm địa chỉ", color = Color.White)
            }
        }
    }
}
