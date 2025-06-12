package com.example.lcshop.config

object Constants {
    // Network Configuration
    // const val BASE_URL = "http://10.0.2.2:3000/api/auth/"
    //const val BASE_URL = "http://192.168.1.6:3000/api/auth/"
    private const val SERVER_IP = "192.168.1.3" // 6IP Mạng của máy chủ
    //private const val SERVER_IP = "10.0.2.2"
    private const val SERVER_PORT = "3000"

    // Base URLs
    const val BASE_URL = "http://$SERVER_IP:$SERVER_PORT/"
    const val AUTH_BASE_URL = "${BASE_URL}api/auth/"
    const val PRODUCT_BASE_URL = "${BASE_URL}api/"
    const val API_BASE_URL = "${BASE_URL}api/"

    // Endpoints
    object Endpoints {
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val LOGOUT = "logout"
        const val REFRESH_TOKEN = "refresh"
    }

    // Network Settings
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}