package com.example.lcshop
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iotconnectmart_admin.viewmodel.AuthViewModelFactory
import com.example.lcshop.repository.AuthRepository
import com.example.lcshop.util.PreferenceManager
import com.example.lcshop.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToHome: () -> Unit

) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )
    var username by remember { mutableStateOf("1test@example.com") }
    var password by remember { mutableStateOf("password123") }
    val authResponse by viewModel.authResponse.observeAsState()
    val error by viewModel.error.observeAsState()


    val preferenceManager = PreferenceManager(context)

    // Chuyển màn hình khi đăng nhập thành công
 LaunchedEffect(authResponse) {
     authResponse?.let { response ->
         if (response.token != null) {
             preferenceManager.setUserToken(response.token ?: "")
             Log.d("LoginScreen", "Token saved: ${response.token}")
             when (response.user?.role_id) {
                 1 -> onNavigateToAdmin()
                 else -> onNavigateToHome()
             }
         }
     }
 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF001F5B))
                .border(width = 2.dp, color = Color(0xFF1976D2))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LC Shop",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Đăng nhập tài khoản", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nhập Email Đăng nhập") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Nhập mật khẩu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(

                    onClick = {
                        viewModel.login(username, password)
                        Log.d("LoginScreen", "Người dùng bấm nút Đăng nhập")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B)),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(48.dp)

                ) {

                    Text("Đăng nhập" , color = Color.White )
                    Log.d("LoginScreen", "Token saved: ${authResponse?.token}")

                }

                // Hiển thị lỗi nếu có
                if (error != null) {
                    Text(text = error ?: "", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClickableText(
                        text = AnnotatedString("Quên mật khẩu?"),
                        onClick = { onNavigateToForgotPassword() },
                        style = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ClickableText(
                        text = AnnotatedString("Bạn chưa có tài khoản? Đăng ký tại đây"),
                        onClick = { onNavigateToRegister() },
                        style = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                }
            }
        }
    }
}