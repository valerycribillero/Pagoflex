package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.BotonSecundario
import com.example.app.ui.components.CampoTexto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetalleScreen(
    compromiso: Compromiso,
    onVolver: () -> Unit,
    onPagar: (String) -> Boolean,
    onReportar: (String) -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var mostrarReporte by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf("") }
    var errorPago by remember { mutableStateOf<String?>(null) }
    val vencido = compromiso.estado == "Vencido"
    val total = compromiso.monto + if (vencido) compromiso.recargo else 0

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextButton(onClick = onVolver) { Text("Volver") }
        Text(compromiso.empresa)
        Text(compromiso.concepto)
        Text("Folio: ${compromiso.folio}")
        Text("Monto: $${compromiso.monto}")
        if (vencido) {
            Text("Recargo por atraso: $${compromiso.recargo}")
            Text("Total a pagar: $$total")
        }
        Text("Vencimiento: ${fechaVisible(compromiso.vencimiento)}")
        Text("Estado: ${compromiso.estado}")
        if (compromiso.estado == "Pendiente" || vencido) {
            BotonPrincipal("Pagar $$total", { mostrarConfirmacion = true }, Modifier.fillMaxWidth())
            BotonSecundario("Reportar problema", { mostrarReporte = true }, Modifier.fillMaxWidth())
        } else if (compromiso.estado == "En revisión" || compromiso.estado == "Anulado") {
            Text("Este compromiso no se puede pagar mientras esté ${compromiso.estado.lowercase()}.")
        }
        errorPago?.let { Text(it) }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar pago") },
            text = {
                Column {
                    Text("Pagarás el total de $${total} a ${compromiso.empresa}.")
                    Text("Medio de pago: Tarjeta de débito")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val realizado = onPagar("Tarjeta de débito")
                    if (realizado) {
                        mostrarConfirmacion = false
                        errorPago = null
                    } else {
                        mostrarConfirmacion = false
                        errorPago = "Debes pagar primero el compromiso más antiguo de esta empresa."
                    }
                }) { Text("Confirmar pago") }
            },
            dismissButton = { TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") } }
        )
    }

    if (mostrarReporte) {
        AlertDialog(
            onDismissRequest = { mostrarReporte = false },
            title = { Text("Reportar problema") },
            text = { CampoTexto(motivo, "Motivo", { motivo = it }) },
            confirmButton = {
                TextButton(onClick = {
                    if (motivo.isNotBlank()) {
                        onReportar(motivo)
                        mostrarReporte = false
                    }
                }) { Text("Enviar") }
            },
            dismissButton = { TextButton(onClick = { mostrarReporte = false }) { Text("Cancelar") } }
        )
    }
}

private fun fechaVisible(fecha: String): String = try {
    val date = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fecha)
    } catch (_: Exception) {
        SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(fecha)
    }
    date?.let {
        SimpleDateFormat("dd-MM-yyyy", Locale("es", "CL")).format(it)
    } ?: fecha
} catch (_: Exception) { fecha }
