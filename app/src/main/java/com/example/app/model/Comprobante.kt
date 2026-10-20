package com.example.app.model

data class Comprobante(
    val numero: String,
    val folioCompromiso: String,
    val empresa: String,
    val concepto: String,
    val montoPagado: Int,
    val fechaHora: String,
    val canal: String = "App",
    val medioPago: String = "Tarjeta de débito"
) {
    val medioPagoVisible: String
        get() = if (medioPago.equals("Pago simulado", ignoreCase = true)) {
            "Tarjeta de débito"
        } else {
            medioPago
        }
}
