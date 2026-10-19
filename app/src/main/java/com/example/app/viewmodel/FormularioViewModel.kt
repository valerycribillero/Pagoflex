package com.example.app.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.app.model.FormularioUiState
import com.example.app.model.Usuario
import com.example.app.ui.screens.FormularioContent
import com.example.app.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun onNombreCambia(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null, errorRegistro = null, registroExitoso = false) }
    }

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor, errorRut = null, errorRegistro = null, registroExitoso = false) }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, errorRegistro = null, registroExitoso = false) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                contrasena = valor.take(6),
                errorContrasena = null,
                errorRegistro = null,
                registroExitoso = false
            )
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onTerminosCambia(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor, errorTerminos = null) }
    }

    fun onLimpiar() {
        _uiState.value = FormularioUiState()
    }

    fun onRegistrar(): Usuario? {
        val estado = _uiState.value
        val errorNombre = if (estado.nombre.isBlank()) "El nombre es obligatorio" else null
        val errorRut = if (!estado.rut.contains(".") || !estado.rut.contains("-"))
            "Ingresa el RUT con puntos y guion" else null
        val errorCorreo = if (!estado.correo.contains("@") || !estado.correo.contains("."))
            "Ingresa un correo válido" else null
        val errorContrasena = if (estado.contrasena.isBlank())
            "La contraseña es obligatoria" else null
        val errorTerminos = if (!estado.aceptaTerminos) "Debes aceptar los términos" else null

        val hayErrores = listOf(
            errorNombre, errorRut, errorCorreo, errorContrasena, errorTerminos
        ).any { it != null }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorRut = errorRut,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorTerminos = errorTerminos,
                registroExitoso = !hayErrores
            )
        }

        if (hayErrores) return null
        return Usuario(
            rut = estado.rut.trim(),
            nombre = estado.nombre.trim(),
            correo = estado.correo.trim().lowercase(),
            rolId = 1L,
            rolNombre = "CLIENTE"
        )
    }

    fun onRegistroFallido(mensaje: String) {
        _uiState.update {
            it.copy(errorRegistro = mensaje, registroExitoso = false)
        }
    }
}
