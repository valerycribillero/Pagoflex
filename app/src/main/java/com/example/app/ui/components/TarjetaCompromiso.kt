package com.example.app.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.ui.components.EtiquetaEstado
import com.example.app.ui.styles.EstilosTarjeta

@Composable
fun TarjetaCompromiso(compromiso: Compromiso, alPresionar: () -> Unit = {}) {
    Card(onClick = alPresionar, shape = EstilosTarjeta.forma, colors = EstilosTarjeta.colores()) {
        Column(Modifier.padding(14.dp)) {
            Text(compromiso.empresa, style = MaterialTheme.typography.labelMedium)
            Text(compromiso.concepto, style = MaterialTheme.typography.titleMedium)
            val total = compromiso.monto + if (compromiso.estado == "Vencido") compromiso.recargo else 0
            Text("${compromiso.usuario.nombre} · $$total")
            if (compromiso.estado == "Vencido") Text("Incluye recargo de $${compromiso.recargo}")
            Text("Vence ${fechaBreve(compromiso.vencimiento)}")
            EtiquetaEstado(compromiso.estado)
        }
    }
}

private fun fechaBreve(fecha: String): String = try {
    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(fecha)?.let {
        java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale("es", "CL")).format(it)
    } ?: fecha
} catch (_: Exception) { fecha }
