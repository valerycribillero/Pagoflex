package com.example.app.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.example.app.ui.styles.EstilosBoton
import com.example.app.ui.theme.AppTheme

@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = EstilosBoton.forma,
        colors = EstilosBoton.colores()
    ) { Text(texto) }
}

@Preview(showBackground = true)
@Composable
private fun BotonPrincipalPreview() {
    AppTheme {
        BotonPrincipal("Continuar", {}, Modifier.padding(16.dp).fillMaxWidth())
    }
}
