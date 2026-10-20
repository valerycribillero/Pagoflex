package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.app.model.Usuario
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.CampoContrasena
import com.example.app.ui.components.CampoTexto
import com.example.app.ui.theme.AppThemePreview
import com.example.app.ui.theme.Primario
import com.example.app.ui.theme.Secundario

@Composable
fun LoginScreen(
    onIniciarSesion: (String, String) -> Usuario?,
    onLoginExitoso: (Usuario) -> Unit,
    onCrearCuenta: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Primario)) { append("Pago") }
                    withStyle(SpanStyle(color = Secundario)) { append("Flex") }
                },
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(8.dp))
            Text("Inicia sesión con tu correo y contraseña.")
            Spacer(Modifier.height(24.dp))

            CampoTexto(
                valor = correo,
                etiqueta = "Correo electrónico",
                alCambiar = {
                    correo = it
                    error = null
                }
            )
            Spacer(Modifier.height(12.dp))
            CampoContrasena(
                valor = contrasena,
                alCambiar = {
                    contrasena = it.take(6)
                    error = null
                },
                visible = contrasenaVisible,
                alAlternarVisible = { contrasenaVisible = !contrasenaVisible }
            )
            error?.let { mensaje ->
                Spacer(Modifier.height(8.dp))
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            BotonPrincipal(
                texto = "Iniciar sesión",
                onClick = {
                    val usuario = onIniciarSesion(correo, contrasena)
                    if (usuario == null) {
                        error = "Correo o contraseña incorrectos."
                    } else {
                        error = null
                        onLoginExitoso(usuario)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = onCrearCuenta,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }
        }
    }
}

@Preview(name = "Claro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Oscuro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginPreview() {
    AppThemePreview {
        LoginScreen(
            onIniciarSesion = { _, _ -> null },
            onLoginExitoso = {},
            onCrearCuenta = {}
        )
    }
}
