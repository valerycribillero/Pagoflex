package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.example.app.model.Compromiso
import com.example.app.model.Reporte
import com.example.app.model.Usuario
import com.example.app.ui.components.TarjetaEstadistica
import com.example.app.ui.components.TarjetaSeccion
import com.example.app.ui.theme.Primario
import com.example.app.ui.theme.Secundario
import com.example.app.ui.theme.AppThemePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    compromisos: List<Compromiso>,
    cantidadPagos: Int,
    onVolver: () -> Unit,
    reportes: List<Reporte> = emptyList()
) {
    val esAgente = usuario.rolNombre == "AGENTE"
    val pendientes = compromisos.count {
        it.estado == "Pendiente" || it.estado == "Vencido"
    }
    val reportesActivos = reportes.count { it.estado != "Resuelto" }
    val iniciales = usuario.nombre.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Volver",
                            tint = Secundario
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { espacio ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacio)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Portada y avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .background(Primario),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(R.drawable.portada_perfil),
                    contentDescription = "Imagen de portada del perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 42.dp)
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(Secundario)
                        .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(52.dp))
            Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
            Text(
                text = "${usuario.rolNombre} · ${usuario.correo}",
                style = MaterialTheme.typography.bodyMedium,
                color = Secundario,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "RUT: ${usuario.rut}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (esAgente) {
                    TarjetaEstadistica("${reportes.size}", "Reportes", Modifier.weight(1f))
                    TarjetaEstadistica("$reportesActivos", "Pendientes", Modifier.weight(1f))
                    TarjetaEstadistica(
                        "${reportes.count { it.estado == "Resuelto" }}",
                        "Resueltos",
                        Modifier.weight(1f)
                    )
                } else {
                    TarjetaEstadistica(compromisos.size.toString(), "Servicios", Modifier.weight(1f))
                    TarjetaEstadistica(pendientes.toString(), "Pendientes", Modifier.weight(1f))
                    TarjetaEstadistica(cantidadPagos.toString(), "Pagos", Modifier.weight(1f))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaSeccion(
                    titulo = "Sobre mí",
                    modifier = Modifier.heightIn(min = 112.dp)
                ) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (esAgente) "Soy agente de atención y ayudo a resolver los reportes de PagoFlex."
                        else "Soy cliente frecuente y me gusta pagar al día.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TarjetaSeccion(
                    titulo = if (esAgente) "Mi actividad" else "Mis compromisos",
                    modifier = Modifier.heightIn(min = 120.dp)
                ) {
                    Spacer(Modifier.height(8.dp))
                    val nombres = compromisos.map { it.empresa }.distinct()
                    if (esAgente) {
                        Text("Reportes en proceso: ${reportes.count { it.estado == "En proceso" }}")
                    } else if (nombres.isEmpty()) {
                        Text("Todavía no tienes compromisos registrados.")
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            nombres.take(3).forEach { nombre ->
                                Text(
                                    text = nombre,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Preview(name = "Perfil claro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Perfil oscuro", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PerfilPreview() {
    AppThemePreview {
        PerfilScreen(
            usuario = Usuario(
                id = 1,
                rut = "20.435.214-5",
                nombre = "Sofía Pérez",
                correo = "sofia@gmail.com",
                rolId = 1,
                rolNombre = "CLIENTE"
            ),
            compromisos = listOf(
                Compromiso(
                    folio = "PF-001",
                    empresa = "Universidad",
                    usuario = Usuario(1, "20.435.214-5", "Sofía Pérez", "sofia@gmail.com", 1, "CLIENTE"),
                    concepto = "Cuota mensual",
                    monto = 350000,
                    vencimiento = "2026-10-15"
                ),
                Compromiso(
                    folio = "PF-002",
                    empresa = "Enel",
                    usuario = Usuario(1, "20.435.214-5", "Sofía Pérez", "sofia@gmail.com", 1, "CLIENTE"),
                    concepto = "Electricidad",
                    monto = 28000,
                    vencimiento = "2026-10-20"
                )
            ),
            cantidadPagos = 1,
            onVolver = {}
        )
    }
}
