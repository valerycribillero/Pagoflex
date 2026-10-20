package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.model.Servicio
import com.example.app.model.Usuario
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.TarjetaCompromiso
import com.example.app.ui.theme.AppThemePreview
import com.example.app.ui.theme.Primario
import com.example.app.ui.theme.PrimarioClaro
import com.example.app.ui.theme.PrimarioOscuro
import com.example.app.ui.theme.Secundario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    usuario: Usuario?,
    compromisos: List<Compromiso>,
    servicios: List<Servicio>,
    onVolver: () -> Unit,
    onVerDetalle: (String) -> Unit,
    onVerPerfil: () -> Unit,
    onAgregarServicio: () -> Unit,
    onEliminarServicios: () -> Unit,
    onVerHistorial: () -> Unit,
    onReportarProblema: () -> Unit,
    onVerHistorialReportes: () -> Unit
) {
    val pendientes = compromisos.filter { it.estado == "Pendiente" || it.estado == "Vencido" }
    val totalPendiente = pendientes.sumOf { it.monto + if (it.estado == "Vencido") it.recargo else 0 }
    val proximoCompromiso = pendientes.firstOrNull()
    val modoOscuro = isSystemInDarkTheme()
    val colorFondoResumen = if (modoOscuro) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Primario
    }
    val colorTextoResumen = if (modoOscuro) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        Color.White
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Primario)) { append("Pago") }
                        withStyle(SpanStyle(color = Secundario)) { append("Flex") }
                    },
                    modifier = Modifier.padding(20.dp)
                )

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Mi perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerPerfil()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Agregar servicio") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAgregarServicio()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Eliminar servicios") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onEliminarServicios()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Historial de pagos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerHistorial()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Reportar problema") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onReportarProblema()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Historial de reportes") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerHistorialReportes()
                    }
                )

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVolver()
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(color = Primario)) { append("Pago") }
                            withStyle(SpanStyle(color = Secundario)) { append("Flex") }
                        })
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = Secundario
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Text("☰", color = Secundario)
                        }
                    }
                )
            }
        ) { espacio ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacio)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column {
                        Text("Hola, ${usuario?.nombre ?: "cliente"}")
                        Text("Aquí tienes un resumen de tus pagos")
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colorFondoResumen)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Total pendiente", color = colorTextoResumen)
                            Text(formatearMonto(totalPendiente), style = MaterialTheme.typography.headlineSmall, color = colorTextoResumen)
                            Surface(
                                color = if (modoOscuro) Primario else PrimarioClaro,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    "Próximo vencimiento",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (modoOscuro) Color.White else Primario
                                )
                            }
                            Text(proximoCompromiso?.let { fechaVisibleInicio(it.vencimiento) } ?: "Sin pagos pendientes", color = colorTextoResumen)
                        }
                    }
                }

                item {
                    Text("Tus próximos compromisos:")
                }

                if (pendientes.isEmpty()) {
                    item {
                        Text("No tienes compromisos pendientes.")
                    }
                } else {
                    items(pendientes) { compromiso ->
                        TarjetaCompromiso(
                            compromiso = compromiso,
                            alPresionar = {
                                onVerDetalle(compromiso.folio)
                            }
                        )
                    }

                }
            }
        }
    }
}

private fun formatearMonto(monto: Int): String =
    "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale("es", "CL")).format(monto)

private fun fechaVisibleInicio(fecha: String): String = try {
    val date = try {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(fecha)
    } catch (_: Exception) {
        java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.US).parse(fecha)
    }
    date?.let {
        java.text.SimpleDateFormat("dd 'de' MMMM 'de' yyyy", java.util.Locale("es", "CL")).format(it)
    } ?: fecha
} catch (_: Exception) { fecha }


//Vista previa del inicio
@Preview(name = "Inicio claro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Inicio oscuro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InicioPreview() {
    AppThemePreview {
        val usuario = Usuario(
            rut = "20.435.214-5",
            nombre = "Sofía",
            correo = "sofia@duocuc.cl",
            rolId = 1L,
            rolNombre = "CLIENTE"
        )

        val compromisos = listOf(
            Compromiso(
                folio = "PF-001",
                empresa = "Duoc Uc",
                usuario = usuario,
                concepto = "Mensualidad",
                monto = 356000,
                vencimiento = "2026-10-25"
            ),
            Compromiso(
                folio = "PF-002",
                empresa = "Enel",
                usuario = usuario,
                concepto = "Cuenta de luz",
                monto = 28000,
                vencimiento = "2026-10-20"
            )
        )

        InicioScreen(
            usuario = usuario,
            compromisos = compromisos,
            servicios = listOf(
                Servicio(
                    usuarioId = usuario.id,
                    nombreEmpresa = "Instituto Profesional",
                    rutPagador = "20.435.214-5",
                    alias = "Mensualidad"
                )
            ),
            onVolver = {},
            onVerDetalle = {},
            onVerPerfil = {},
            onAgregarServicio = {},
            onEliminarServicios = {},
            onVerHistorial = {},
            onReportarProblema = {},
            onVerHistorialReportes = {}
        )
    }
}
