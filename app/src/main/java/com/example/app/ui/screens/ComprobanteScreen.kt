package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Comprobante
import com.example.app.ui.components.BotonPrincipal

@Composable
fun ComprobanteScreen(comprobante: Comprobante?, onVolver: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (comprobante == null) "No se encontró el comprobante" else "¡Pago realizado!")
        if (comprobante != null) {
            Text("Comprobante ${comprobante.numero}")
            Text("Empresa: ${comprobante.empresa}")
            Text("Compromiso: ${comprobante.folioCompromiso}")
            Text("Concepto: ${comprobante.concepto}")
            Text("Total pagado: $${comprobante.montoPagado}")
            Text("Fecha: ${comprobante.fechaHora}")
            Text("Canal: ${comprobante.canal}")
            Text("Medio de pago: ${comprobante.medioPagoVisible}")
            Text("Guarda este número para consultar el pago.")
        }
        BotonPrincipal("Volver", onVolver, Modifier.fillMaxWidth())
    }
}
