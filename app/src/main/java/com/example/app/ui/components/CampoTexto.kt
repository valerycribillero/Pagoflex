package com.example.app.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.example.app.ui.styles.EstilosCampo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.ui.theme.AppTheme

@Composable
fun CampoTexto(
    valor: String,
    etiqueta: String,
    alCambiar: (String) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true,
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}

@Preview(showBackground = true)
@Composable
fun CampoTextoPreview() {
    AppTheme {
        CampoTexto(
            valor = "Sofía Pérez",
            etiqueta = "Nombre completo",
            alCambiar = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
