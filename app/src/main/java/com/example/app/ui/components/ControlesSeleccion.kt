package com.example.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControlesSeleccion(opciones: List<String>, alSeleccionar: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.forEachIndexed { indice, opcion ->
            OutlinedButton(onClick = { alSeleccionar(indice) }, modifier = Modifier.fillMaxWidth()) {
                Text(opcion)
            }
        }
    }
}
