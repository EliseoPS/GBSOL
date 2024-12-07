package com.alparslanguney.example.nfc.models

data class ExcerciseEntry(
    val date: String,
    val exercise_id: Int,
    val id_en: Int,
    val reps: Int,
    val user_id: Int,
    val weight: Int
)