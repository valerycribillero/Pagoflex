package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.app.model.Compromiso
import com.example.app.model.Reporte
import com.example.app.model.Servicio
import com.example.app.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PagoFlexViewModel : ViewModel() {
    private val _compromisos = MutableStateFlow<List<Compromiso>>(emptyList())
    val compromisos: StateFlow<List<Compromiso>> = _compromisos.asStateFlow()

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuarioActual.value = usuario
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        _servicios.value = emptyList()
    }

    fun cargarServicios(servicios: List<Servicio>) {
        _servicios.value = servicios
    }

    fun cargarCompromisos(compromisos: List<Compromiso>) {
        _compromisos.value = compromisos
    }

    fun cargarReportes(reportes: List<Reporte>) {
        _reportes.value = reportes
    }

    fun agregarServicio(servicio: Servicio) {
        _servicios.update { lista -> listOf(servicio) + lista }
    }

    fun pagar(folio: String) {
        _compromisos.update { lista ->
            lista.map { if (it.folio == folio) it.copy(estado = "Pagado") else it }
        }
    }

}
