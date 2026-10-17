package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Comprobante

@Composable
fun HistorialPagosScreen(
    comprobantes: List<Comprobante>,
    onVolver: () -> Unit,
    onVerComprobante: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextButton(onClick = onVolver) { Text("Volver") }
        Text("Historial de pagos")
        if (comprobantes.isEmpty()) {
            Text("Todavía no tienes pagos registrados.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(comprobantes, key = { it.numero }) { comprobante ->
                    Card(onClick = { onVerComprobante(comprobante.folioCompromiso) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(comprobante.empresa)
                            Text(comprobante.concepto)
                            Text("$${comprobante.montoPagado} · ${comprobante.fechaHora}")
                            Text("Comprobante ${comprobante.numero}")
                        }
                    }
                }
            }
        }
    }
}
