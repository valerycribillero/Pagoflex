package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.FondoClaro
import com.example.app.ui.theme.Morado
import com.example.app.ui.theme.Turquesa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    usuario: Usuario?,
    compromisos: List<Compromiso>,
    servicios: List<Servicio>,
    onVolver: () -> Unit,
    onVerDetalle: (String) -> Unit,
    onAgregarServicio: () -> Unit,
    onVerHistorial: () -> Unit,
    onReportarProblema: () -> Unit
) {
    val pendientes = compromisos.filter { it.estado == "Pendiente" || it.estado == "Vencido" }
    val totalPendiente = pendientes.sumOf { it.monto + if (it.estado == "Vencido") it.recargo else 0 }
    val proximoCompromiso = pendientes.firstOrNull()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Turquesa)) { append("Pago") }
                        withStyle(SpanStyle(color = Morado)) { append("Flex") }
                    },
                    modifier = Modifier.padding(20.dp)
                )

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Agregar servicio") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onAgregarServicio()
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
            containerColor = FondoClaro,
            topBar = {
                TopAppBar(
                    title = {
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(color = Turquesa)) { append("Pago") }
                            withStyle(SpanStyle(color = Morado)) { append("Flex") }
                        })
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        navigationIconContentColor = Morado
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Text("☰", color = Morado)
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
                        colors = CardDefaults.cardColors(containerColor = Turquesa)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Total pendiente", color = Color.White)
                            Text(formatearMonto(totalPendiente), style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            Text("Próximo vencimiento", color = Color.White)
                            Text(proximoCompromiso?.let { fechaVisibleInicio(it.vencimiento) } ?: "Sin pagos pendientes", color = Color.White)
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
    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(fecha)?.let {
        java.text.SimpleDateFormat("dd 'de' MMM 'de' yyyy", java.util.Locale("es", "CL")).format(it)
    } ?: fecha
} catch (_: Exception) { fecha }

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioPreview() {
    AppTheme {
        val usuario = Usuario(
            rut = "12.345.678-5",
            nombre = "Sofía",
            correo = "sofia@pagoflex.cl",
            rolId = 1L,
            rolNombre = "CLIENTE"
        )

        val compromisos = listOf(
            Compromiso(
                folio = "PF-001",
                empresa = "Universidad",
                usuario = usuario,
                concepto = "Mensualidad",
                monto = 35000,
                vencimiento = "15-10-2026"
            ),
            Compromiso(
                folio = "PF-002",
                empresa = "Luz",
                usuario = usuario,
                concepto = "Cuenta de luz",
                monto = 28000,
                vencimiento = "20-10-2026"
            )
        )

        InicioScreen(
            usuario = usuario,
            compromisos = compromisos,
            servicios = listOf(
                Servicio(
                    usuarioId = usuario.id,
                    nombreEmpresa = "Universidad",
                    rutPagador = "12.345.678-5",
                    alias = "Mensualidad"
                )
            ),
            onVolver = {},
            onVerDetalle = {},
            onAgregarServicio = {},
            onVerHistorial = {},
            onReportarProblema = {}
        )
    }
}
