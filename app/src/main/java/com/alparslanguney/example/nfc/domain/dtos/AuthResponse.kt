package com.alparslanguney.example.nfc.domain.dtos

class AuthResponse(
    val userId : Int,
    val isLogged : Boolean,
    val message : String
)