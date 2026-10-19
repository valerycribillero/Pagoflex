package com.example.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.model.Usuario
import com.example.app.ui.styles.EstilosTarjeta
import com.example.app.ui.theme.AppTheme

@Composable
fun TarjetaCompromiso(
    compromiso: Compromiso,
    alPresionar: () -> Unit = {}) {
    Card(
        onClick = alPresionar,
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.colores(),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(compromiso.empresa, style = MaterialTheme.typography.titleLarge)
            Text(compromiso.concepto, style = MaterialTheme.typography.titleMedium)
            val total = compromiso.monto + if (compromiso.estado == "Vencido") compromiso.recargo else 0
            Text(formatearMonto(total))
            if (compromiso.estado == "Vencido") Text("Incluye recargo de ${formatearMonto(compromiso.recargo)}")
            Text("Vence ${fechaBreve(compromiso.vencimiento)}")
            EtiquetaEstado(compromiso.estado)
        }
    }
}

private fun formatearMonto(monto: Int): String =
    "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(monto)

private fun fechaBreve(fecha: String): String = try {
    val date = try {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(fecha)
    } catch (_: Exception) {
        java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.US).parse(fecha)
    }
    date?.let {
        java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale("es", "CL")).format(it)
    } ?: fecha
} catch (_: Exception) { fecha }

@Preview(showBackground = true)
@Composable
private fun TarjetaCompromisoPreview() {
    AppTheme {
        Column(Modifier.padding(16.dp)) {
            TarjetaCompromiso(
                compromiso = Compromiso(
                    folio = "12345",
                    empresa = "Enel",
                    usuario = Usuario(1, "12.345.678-9", "Juan Pérez", "sofia@duocuc.cl", 1),
                    concepto = "Luz",
                    monto = 35000,
                    vencimiento = "2025-05-20",
                    estado = "Pendiente"
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaCompromisoVencidoPreview() {
    AppTheme {
        Column(Modifier.padding(16.dp)) {
            TarjetaCompromiso(
                compromiso = Compromiso(
                    folio = "67890",
                    empresa = "Aguas Andinas",
                    usuario = Usuario(1, "12.345.678-9", "Juan Pérez", "sofia@duocuc.cl", 1),
                    concepto = "Agua",
                    monto = 18000,
                    vencimiento = "2025-04-10",
                    estado = "Vencido",
                    recargo = 2500
                )
            )
        }
    }
}


