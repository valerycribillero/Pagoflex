package com.example.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.app.ui.theme.AppTheme

@Composable
fun CasillaConTexto(
    texto: String,
    marcada: Boolean,
    alCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = marcada, onCheckedChange = alCambiar)
        Text(texto)
    }
}

@Preview(showBackground = true)
@Composable
private fun CasillaConTextoPreview() {
    AppTheme {
        CasillaConTexto("Acepto los términos y condiciones", true, {})
    }
}
