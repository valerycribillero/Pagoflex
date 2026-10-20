package com.example.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.app.ui.theme.AppTheme

@Composable
fun EtiquetaEstado(estado: String, modifier: Modifier = Modifier) {
    val colorFondo = when (estado) {
        "Pendiente", "Vencido" -> MaterialTheme.colorScheme.primaryContainer
        "Pagado" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val colorTexto = when (estado) {
        "Pendiente", "Vencido" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Pagado" -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        color = colorFondo,
        contentColor = colorTexto,
        shape = MaterialTheme.shapes.small
    ) {
        Text(estado, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EtiquetaEstadoPreview() {
    AppTheme {
        EtiquetaEstado("Pendiente", Modifier.padding(16.dp))
    }
}
