package com.example.lcshop
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

import com.example.iotconnectmart_admin.viewmodel.AuthViewModelFactory
import com.example.lcshop.repository.AuthRepository
import com.example.lcshop.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (() -> Unit)? = null
) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )

    var username by remember { mutableStateOf("cuong1") }
    var email by remember { mutableStateOf("mcuon@gmail.com") }
    var password by remember { mutableStateOf("123456") }
    var fullname by remember { mutableStateOf("Cuong") }
    var phone by remember { mutableStateOf("012393766") }

    val authResponse by viewModel.authResponse.observeAsState()
    val error by viewModel.error.observeAsState()

    // Call onRegisterSuccess when registration is successful
    LaunchedEffect(authResponse) {
        if (authResponse != null) {
            onRegisterSuccess?.invoke()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Đăng ký tài khoản", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên đăng nhập") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullname,
                    onValueChange = { fullname = it },
                    label = { Text("Họ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Tên") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.register(username, email, password, fullname, phone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F5B))
                ) {
                    Text("Đăng ký", color = Color.White)
                }

                // Show error if exists
                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = error ?: "", color = Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}