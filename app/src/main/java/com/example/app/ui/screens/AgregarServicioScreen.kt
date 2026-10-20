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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.R
import com.example.app.model.Servicio
import com.example.app.model.ServicioUiState
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.CampoTexto
import com.example.app.ui.components.TarjetaSeccion
import com.example.app.ui.theme.AppThemePreview
import com.example.app.ui.theme.Secundario
import com.example.app.viewmodel.ServicioViewModel

@Composable
fun AgregarServicioScreen(
    usuarioId: Long,
    onVolver: () -> Unit,
    onServicioGuardado: () -> Unit,
    onGuardarServicio: (Servicio) -> Boolean,
    viewModel: ServicioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AgregarServicioContent(
        uiState = uiState,
        onVolver = onVolver,
        onNombreEmpresaCambia = viewModel::onNombreEmpresaCambia,
        onRutPagadorCambia = viewModel::onRutPagadorCambia,
        onAliasCambia = viewModel::onAliasCambia,
        onAgregar = {
            val servicio = viewModel.onAgregar(usuarioId)
            if (servicio != null) {
                if (onGuardarServicio(servicio)) {
                    onServicioGuardado()
                } else {
                    viewModel.onGuardadoFallido()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarServicioContent(
    uiState: ServicioUiState,
    onVolver: () -> Unit,
    onNombreEmpresaCambia: (String) -> Unit,
    onRutPagadorCambia: (String) -> Unit,
    onAliasCambia: (String) -> Unit,
    onAgregar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar servicio") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Volver",
                            tint = Secundario
                        )
                    }
                }
            )
        }
    ) { espacio ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ingresa los datos del servicio que quieres agregar.")

            TarjetaSeccion(titulo = "Datos del servicio") {
                CampoTexto(
                    valor = uiState.nombreEmpresa,
                    etiqueta = "Nombre de la empresa",
                    alCambiar = onNombreEmpresaCambia,
                    error = uiState.errorNombreEmpresa
                )
                CampoTexto(
                    valor = uiState.rutPagador,
                    etiqueta = "RUT",
                    alCambiar = onRutPagadorCambia,
                    error = uiState.errorRutPagador
                )
                CampoTexto(
                    valor = uiState.alias,
                    etiqueta = "Descripción o alias",
                    alCambiar = onAliasCambia,
                    error = uiState.errorAlias
                )
                Text(
                    "Usa un nombre fácil de reconocer para identificar este compromiso.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            uiState.errorRegistro?.let { mensaje ->
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(4.dp))
            BotonPrincipal(
                texto = "Agregar servicio",
                onClick = onAgregar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgregarServicioPreview() {
    AppThemePreview {
        AgregarServicioContent(
            uiState = ServicioUiState(),
            onVolver = {},
            onNombreEmpresaCambia = {},
            onRutPagadorCambia = {},
            onAliasCambia = {},
            onAgregar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AgregarServicioConErroresPreview() {
    AppThemePreview {
        AgregarServicioContent(
            uiState = ServicioUiState(
                errorNombreEmpresa = "El nombre de la empresa es obligatorio",
                errorRutPagador = "Ingresa un RUT válido con puntos y guion",
                errorAlias = "El alias es obligatorio"
            ),
            onVolver = {},
            onNombreEmpresaCambia = {},
            onRutPagadorCambia = {},
            onAliasCambia = {},
            onAgregar = {}
        )
    }
}
