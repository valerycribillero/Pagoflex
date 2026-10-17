package com.example.app.model

data class Servicio(
    val id: Long = 0,
    val usuarioId: Long,
    val nombreEmpresa: String,
    val rutPagador: String,
    val alias: String
)
