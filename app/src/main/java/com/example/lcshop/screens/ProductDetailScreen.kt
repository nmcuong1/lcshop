//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import coil.compose.AsyncImage
//import com.example.lcshop.R
//import com.example.lcshop.data.model.Product
//import com.example.lschop.repository.ProductRepository
//
//@Composable
//fun ProductDetail_Screen(navController: NavHostController, productId: Int?) {
//    if (productId == null) {
//        Text("Không tìm thấy sản phẩm")
//        return
//    }
//
//    val repository = remember { ProductRepository() }
//    var product by remember { mutableStateOf<Product?>(null) }
//    var error by remember { mutableStateOf<String?>(null) }
//
//    var selectedColor by remember { mutableStateOf<Color?>(null) }
//    var selectedSize by remember { mutableStateOf<String?>(null) }
//    var quantity by remember { mutableStateOf(1) }
//
//    LaunchedEffect(productId) {
//        try {
//            val allProducts = repository.getProducts()
//            product = allProducts.find { it.id == productId }
//            product?.let {
//                // Mặc định chọn màu & size đầu tiên nếu có
//                selectedColor = it.colors?.firstOrNull()?.let { colorHex -> Color(android.graphics.Color.parseColor(colorHex)) }
//                selectedSize = it.sizes?.firstOrNull()
//            }
//        } catch (e: Exception) {
//            error = e.message
//        }
//    }
//
//    if (error != null) {
//        Text("Lỗi: $error")
//    } else if (product == null) {
//        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//    } else {
//        Column(modifier = Modifier.padding(16.dp)) {
//            AsyncImage(
//                model = product!!.img_url,
//                contentDescription = product!!.product_name,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                placeholder = painterResource(id = R.drawable.placeholder_image),
//                error = painterResource(id = R.drawable.placeholder_image)
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text("Tên: ${product!!.product_name}", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
//            Text("Giá: ${product!!.price} đ", fontSize = 18.sp, color = Color(0xFFD32F2F))
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Mô tả: ${product!!.description}", fontSize = 16.sp)
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Màu sắc chọn
//            Text("Màu sắc:", fontSize = 16.sp)
//            Row(modifier = Modifier.padding(vertical = 8.dp)) {
//                product!!.colors?.forEach { colorHex ->
//                    val color = try {
//                        Color(android.graphics.Color.parseColor(colorHex))
//                    } catch (e: Exception) {
//                        Color.Gray
//                    }
//                    Box(
//                        modifier = Modifier
//                            .size(36.dp)
//                            .background(color, shape = RoundedCornerShape(6.dp))
//                            .border(
//                                width = if (selectedColor == color) 3.dp else 1.dp,
//                                color = if (selectedColor == color) Color.Black else Color.Gray,
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .clickable { selectedColor = color }
//                    )
//                    Spacer(modifier = Modifier.width(12.dp))
//                }
//            }
//
//            // Kích thước chọn
//            Text("Kích thước:", fontSize = 16.sp)
//            Row(modifier = Modifier.padding(vertical = 8.dp)) {
//                product!!.sizes?.forEach { size ->
//                    Box(
//                        modifier = Modifier
//                            .padding(end = 12.dp)
//                            .background(
//                                if (size == selectedSize) Color(0xFF001F5B) else Color(0xFFF0F0F0),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .clickable { selectedSize = size }
//                            .padding(horizontal = 20.dp, vertical = 10.dp)
//                    ) {
//                        Text(
//                            size,
//                            fontSize = 16.sp,
//                            color = if (size == selectedSize) Color.White else Color.Black
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Số lượng chọn
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Số lượng:", fontSize = 16.sp)
//                Spacer(modifier = Modifier.width(12.dp))
//                Row(
//                    modifier = Modifier
//                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
//                        .padding(horizontal = 12.dp, vertical = 6.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "-",
//                        fontSize = 26.sp,
//                        modifier = Modifier
//                            .clickable { if (quantity > 1) quantity-- }
//                            .padding(horizontal = 12.dp)
//                    )
//                    Text(
//                        text = quantity.toString(),
//                        fontSize = 20.sp,
//                        modifier = Modifier.padding(horizontal = 12.dp)
//                    )
//                    Text(
//                        text = "+",
//                        fontSize = 26.sp,
//                        modifier = Modifier
//                            .clickable { quantity++ }
//                            .padding(horizontal = 12.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Nút thêm vào giỏ
//            Button(
//                onClick = {
//                    // TODO: xử lý thêm vào giỏ hàng, truyền productId, quantity, selectedColor, selectedSize
//                },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
//            ) {
//                Text("Thêm vào giỏ", color = Color.White, fontSize = 18.sp)
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Nút mua ngay
//            Button(
//                onClick = {
//                    // TODO: xử lý mua ngay (ví dụ chuyển sang màn hình thanh toán)
//                },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
//            ) {
//                Text("Mua ngay", color = Color.White, fontSize = 18.sp)
//            }
//        }
//    }
//}
