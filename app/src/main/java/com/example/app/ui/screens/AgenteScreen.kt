package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Reporte
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.EtiquetaEstado
import com.example.app.ui.components.TarjetaSeccion

@Composable fun AgenteScreen(reportes: List<Reporte>, onVolver: () -> Unit, onResolver: (Reporte) -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        TextButton(onClick = onVolver) { Text("Cerrar sesión") }
        Text("Reportes recibidos")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(reportes) { reporte ->
                TarjetaSeccion("Compromiso: ${reporte.folio}") {
                    Text("Motivo: ${reporte.motivo}")
                    EtiquetaEstado(reporte.estado)
                    if (reporte.estado == "Abierto") {
                        BotonPrincipal("Marcar resuelto", { onResolver(reporte) })
                    }
                }
            }
            if (reportes.isEmpty()) item { Text("Todavía no hay reportes.") }
        }
    }
}
