package com.example.app.model

data class ServicioUiState(
    val nombreEmpresa: String = "",
    val rutPagador: String = "",
    val alias: String = "",
    val errorNombreEmpresa: String? = null,
    val errorRutPagador: String? = null,
    val errorAlias: String? = null,
    val errorRegistro: String? = null
)
