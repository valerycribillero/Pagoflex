package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.model.Usuario
import com.example.app.ui.components.TarjetaCompromiso
import com.example.app.ui.components.TarjetaEstadistica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    usuario: Usuario?,
    compromisos: List<Compromiso>,
    onVolver: () -> Unit,
    onVerDetalle: (String) -> Unit
) {
    val pendientes = compromisos.filter { it.estado == "Pendiente" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis compromisos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("←")
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
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Hola, ${usuario?.nombre ?: "cliente"}")
            }

            item {
                TarjetaEstadistica(
                    etiqueta = "Compromisos pendientes",
                    valor = pendientes.size.toString()
                )
            }

            if (compromisos.isEmpty()) {
                item {
                    Text("Todavía no tienes compromisos registrados.")
                }
            } else {
                items(compromisos) { compromiso ->
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

