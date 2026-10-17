package com.example.app.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.app.ui.styles.EstilosCampo

@Composable
fun CampoContrasena(
    valor: String,
    alCambiar: (String) -> Unit,
    visible: Boolean,
    alAlternarVisible: () -> Unit,
    error: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text("Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            TextButton(onClick = alAlternarVisible) {
                Text(if (visible) "Ocultar" else "Mostrar")
            }
        },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true
    )
}
