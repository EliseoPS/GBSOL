package com.alparslanguney.example.nfc.datasource.services

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GraphService {
    @GET("weights_graph")
    suspend fun getGraph(@Query("user_id") userId : Int, @Query("exercise_id") exerciseId: Int) : Response<ResponseBody>
}