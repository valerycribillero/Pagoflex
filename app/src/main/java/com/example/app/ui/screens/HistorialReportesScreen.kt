package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.model.Reporte
import com.example.app.ui.components.EtiquetaEstado
import com.example.app.ui.components.TarjetaSeccion
import com.example.app.ui.theme.AppThemePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialReportesScreen(
    reportes: List<Reporte>,
    compromisos: List<Compromiso> = emptyList(),
    esAgente: Boolean = false,
    onVolver: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esAgente) "Historial de reportes" else "Mis reportes") },
                navigationIcon = {
                    androidx.compose.material3.TextButton(onClick = onVolver) {
                        Text("‹", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { espacio ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (reportes.isEmpty()) {
                item {
                    Text(
                        if (esAgente) "Todavía no hay reportes en el historial."
                        else "Todavía no has enviado reportes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(reportes.asReversed(), key = { it.folio }) { reporte ->
                    val compromiso = compromisos.find { it.folio == reporte.folio }
                    val tituloCompromiso = compromiso?.let {
                        "${it.empresa} · ${reporte.folio}"
                    } ?: "Compromiso: ${reporte.folio}"
                    TarjetaSeccion(titulo = tituloCompromiso) {
                        Text("Motivo: ${reporte.motivo}")
                        EtiquetaEstado(
                            if (reporte.estado == "Abierto") "Pendiente" else reporte.estado
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Historial reportes claro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Historial reportes oscuro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistorialReportesPreview() {
    AppThemePreview {
        HistorialReportesScreen(
            reportes = listOf(
                Reporte("PF-001", "El monto no corresponde", "Resuelto"),
                Reporte("PF-002", "Ya pagué por otro medio", "En proceso")
            ),
            onVolver = {}
        )
    }
}
