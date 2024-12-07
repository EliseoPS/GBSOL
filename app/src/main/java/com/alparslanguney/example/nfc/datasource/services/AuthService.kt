package com.alparslanguney.example.nfc.datasource.services

import com.alparslanguney.example.nfc.domain.dtos.Auth
import com.alparslanguney.example.nfc.domain.dtos.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body auth: Auth) : Response<AuthResponse>
}