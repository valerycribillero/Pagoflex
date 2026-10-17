package com.example.app.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object CrearCuenta : Ruta("crear-cuenta")
    data object AgregarServicio : Ruta("agregar-servicio")
    data object Usuario : Ruta("usuario")
    data object Agente : Ruta("agente")
    data object Empresa : Ruta("empresa")
    data object Detalle : Ruta("detalle/{folio}") {
        fun crear(folio: String) = "detalle/$folio"
    }
    data object Comprobante : Ruta("comprobante/{folio}") {
        fun crear(folio: String) = "comprobante/$folio"
    }
    data object HistorialPagos : Ruta("historial-pagos")
    data object ReportarProblema : Ruta("reportar-problema")
}
