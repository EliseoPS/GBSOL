package com.alparslanguney.example.nfc.datasource.services

import com.alparslanguney.example.nfc.models.Excercise
import retrofit2.http.GET
import retrofit2.http.Path

interface ExcerciseService {
    @GET("exercises")
    suspend fun getExcercises() : List<Excercise>

    @GET("exercises/{id_ex}")
    suspend fun getExcercisesById(@Path("id_ex") id:Int) : Excercise
}