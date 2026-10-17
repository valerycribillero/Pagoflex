package com.example.app.model

data class FormularioUiState(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val aceptaTerminos: Boolean = false,
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorTerminos: String? = null,
    val errorRegistro: String? = null,
    val registroExitoso: Boolean = false
)
