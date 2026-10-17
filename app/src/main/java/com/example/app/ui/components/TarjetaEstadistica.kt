package com.example.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.ui.styles.EstilosTarjeta

@Composable
fun TarjetaEstadistica(titulo: String, valor: String) {
    Card(shape = EstilosTarjeta.forma, colors = EstilosTarjeta.colores()) {
        Column(Modifier.padding(14.dp)) {
            Text(titulo)
            Text(valor)
        }
    }
}
