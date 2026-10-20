package com.example.app.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import com.example.app.ui.theme.Dimens

object EstilosTarjeta {
    val forma = RoundedCornerShape(Dimens.radioTarjeta)

    @Composable
    fun colores() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    @Composable
    fun elevacion(): CardElevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
}
