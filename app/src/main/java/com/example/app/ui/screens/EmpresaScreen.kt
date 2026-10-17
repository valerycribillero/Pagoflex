package com.example.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.Compromiso
import com.example.app.model.PagoFlexUiState
import com.example.app.ui.components.BotonPrincipal
import com.example.app.ui.components.CampoTexto
import com.example.app.ui.components.TarjetaCompromiso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EmpresaScreen(
    compromisos: List<Compromiso>,
    onVolver: () -> Unit,
    onRegistrar: (String, String, Int, String) -> Boolean
) {
    var formulario by remember { mutableStateOf(PagoFlexUiState()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onVolver) { Text("Cerrar sesión") }
        Text("Empresa · Crédito Andino")
        CampoTexto(formulario.persona, "RUT de la persona", { formulario = formulario.copy(persona = it) })
        CampoTexto(formulario.concepto, "Concepto", { formulario = formulario.copy(concepto = it) })
        CampoTexto(formulario.monto, "Monto", { formulario = formulario.copy(monto = it.filter(Char::isDigit)) })
        CampoTexto(formulario.vencimiento, "Vencimiento (AAAA-MM-DD)", { formulario = formulario.copy(vencimiento = it) })
        Text("Recargo fijo al vencer: $12.000")
        BotonPrincipal(
            texto = "Registrar compromiso",
            onClick = {
                val valor = formulario.monto.toIntOrNull()
                val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                if (formulario.persona.isBlank() || formulario.concepto.isBlank() || valor == null || valor <= 0 || !formulario.vencimiento.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) || formulario.vencimiento < hoy) {
                    mensaje = "Completa los datos y usa una fecha desde hoy."
                } else if (onRegistrar(formulario.persona, formulario.concepto, valor, formulario.vencimiento)) {
                    mensaje = "Compromiso registrado."
                    formulario = PagoFlexUiState()
                } else {
                    mensaje = "No existe un cliente con ese RUT."
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        mensaje?.let { Text(it) }
        Text("Compromisos")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(compromisos, key = { it.folio }) { compromiso -> TarjetaCompromiso(compromiso) }
        }
    }
}
