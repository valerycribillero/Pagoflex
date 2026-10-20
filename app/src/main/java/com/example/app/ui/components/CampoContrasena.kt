package com.example.app.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.example.app.ui.styles.EstilosCampo
import com.example.app.ui.theme.AppTheme

@Composable
fun CampoContrasena(
    valor: String,
    alCambiar: (String) -> Unit,
    visible: Boolean,
    alAlternarVisible: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text("Contraseña") },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_candado),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = alAlternarVisible) {
                Icon(
                    painter = painterResource(
                        if (visible) R.drawable.ic_ojo_tachado else R.drawable.ic_ojo
                    ),
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}
@Preview(showBackground = true)
@Composable
private fun CampoContrasenaPreview() {
    AppTheme {
        CampoContrasena(
            valor = "clave123",
            alCambiar = {},
            visible = false,
            alAlternarVisible = {}
        )
    }
}
