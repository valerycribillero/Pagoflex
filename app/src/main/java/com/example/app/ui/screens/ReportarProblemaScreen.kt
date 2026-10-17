package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.CampoTexto
import com.example.app.ui.components.TarjetaSeccion

@Composable
fun ReportarProblemaScreen(
    compromisos: List<Compromiso>,
    onVolver: () -> Unit,
    onReportar: (String, String) -> Boolean
) {
    val reportables = compromisos.filter { it.estado == "Pendiente" || it.estado == "Vencido" }
    var compromisoSeleccionado by remember { mutableStateOf<Compromiso?>(null) }
    var motivoSeleccionado by remember { mutableStateOf("Ya pagué por otro medio") }
    var comentario by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextButton(onClick = onVolver) { Text("Volver") }
        Text("Reportar problema")
        Text("Elige el compromiso que quieres reportar.")
        if (reportables.isEmpty()) {
            Text("No tienes compromisos disponibles para reportar.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reportables, key = { it.folio }) { compromiso ->
                    TarjetaSeccion(titulo = compromiso.empresa) {
                        Text(compromiso.concepto)
                        Text("Folio: ${compromiso.folio} · $${compromiso.monto}")
                        BotonPrincipal(
                            texto = "Elegir compromiso",
                            onClick = {
                                compromisoSeleccionado = compromiso
                                error = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    compromisoSeleccionado?.let { compromiso ->
        AlertDialog(
            onDismissRequest = { compromisoSeleccionado = null },
            title = { Text("¿Qué problema tienes?") },
            text = {
                Column {
                    Text("${compromiso.empresa} · ${compromiso.concepto}")
                    listOf(
                        "Ya pagué por otro medio",
                        "No reconozco este cobro",
                        "El monto no corresponde",
                        "Otro"
                    ).forEach { opcion ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = motivoSeleccionado == opcion,
                                onClick = { motivoSeleccionado = opcion }
                            )
                            Text(opcion)
                        }
                    }
                    if (motivoSeleccionado == "Otro") {
                        CampoTexto(comentario, "Describe el problema", { comentario = it })
                    }
                    error?.let { Text(it) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (motivoSeleccionado == "Otro" && comentario.isBlank()) {
                        error = "Escribe un comentario para explicar el problema."
                    } else {
                        val motivo = if (motivoSeleccionado == "Otro") "Otro: ${comentario.trim()}" else motivoSeleccionado
                        if (onReportar(compromiso.folio, motivo)) {
                            compromisoSeleccionado = null
                            mostrarConfirmacion = true
                        } else {
                            error = "No se pudo enviar el reporte. Inténtalo nuevamente."
                        }
                    }
                }) { Text("Enviar reporte") }
            },
            dismissButton = {
                TextButton(onClick = { compromisoSeleccionado = null }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false; onVolver() },
            title = { Text("Reporte enviado") },
            text = { Text("El compromiso quedó En revisión y no se puede pagar mientras revisamos el caso.") },
            confirmButton = {
                TextButton(onClick = { mostrarConfirmacion = false; onVolver() }) { Text("Listo") }
            }
        )
    }
}
