package com.example.app.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.app.ui.theme.Dimens

object EstilosBoton {
    val forma = RoundedCornerShape(Dimens.radioBoton)

    @Composable
    fun colores() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
}
