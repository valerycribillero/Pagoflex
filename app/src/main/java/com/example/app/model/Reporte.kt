package com.example.app.model

data class Reporte(
    val folio: String,
    val motivo: String,
    val estado: String = "Pendiente"
)
