package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.app.model.Servicio
import com.example.app.model.ServicioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServicioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ServicioUiState())
    val uiState: StateFlow<ServicioUiState> = _uiState.asStateFlow()

    fun onNombreEmpresaCambia(valor: String) {
        _uiState.update {
            it.copy(nombreEmpresa = valor, errorNombreEmpresa = null, errorRegistro = null)
        }
    }

    fun onRutPagadorCambia(valor: String) {
        _uiState.update {
            it.copy(rutPagador = valor, errorRutPagador = null, errorRegistro = null)
        }
    }

    fun onAliasCambia(valor: String) {
        _uiState.update {
            it.copy(alias = valor, errorAlias = null, errorRegistro = null)
        }
    }

    fun onAgregar(usuarioId: Long): Servicio? {
        val estado = _uiState.value
        val errorNombre = if (estado.nombreEmpresa.isBlank()) {
            "El nombre de la empresa es obligatorio"
        } else null
        val errorRut = if (!estado.rutPagador.contains(".") || !estado.rutPagador.contains("-")) {
            "Ingresa el RUT con puntos y guion"
        } else null
        val errorAlias = if (estado.alias.isBlank()) {
            "Ingresa una descripción o alias"
        } else null

        val hayErrores = listOf(errorNombre, errorRut, errorAlias).any { it != null }
        _uiState.update {
            it.copy(
                errorNombreEmpresa = errorNombre,
                errorRutPagador = errorRut,
                errorAlias = errorAlias
            )
        }

        if (hayErrores) return null

        return Servicio(
            usuarioId = usuarioId,
            nombreEmpresa = estado.nombreEmpresa.trim(),
            rutPagador = estado.rutPagador.trim(),
            alias = estado.alias.trim()
        )
    }

    fun onGuardadoFallido() {
        _uiState.update { it.copy(errorRegistro = "No se pudo guardar el servicio.") }
    }
}
