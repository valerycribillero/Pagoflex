package com.example.app.model

data class Compromiso(
    val folio: String,
    val empresa: String,
    val usuario: Usuario,
    val concepto: String,
    val monto: Int,
    val vencimiento: String,
    val estado: String = "Pendiente",
    val recargo: Int = 0,
    val numeroCuota: String = ""
)
