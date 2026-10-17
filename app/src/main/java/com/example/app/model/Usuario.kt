package com.example.app.model


data class Usuario(
    val id: Long = 0,
    val rut: String,
    val nombre: String,
    val correo: String,
    val rolId: Long,
    val rolNombre: String = ""
)
