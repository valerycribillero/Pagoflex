package com.example.app.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")
    data object CrearCuenta : Ruta("crear-cuenta")
    data object Perfil : Ruta("perfil")
    data object AgregarServicio : Ruta("agregar-servicio")
    data object MisServicios : Ruta("mis-servicios")
    data object Usuario : Ruta("usuario")
    data object Agente : Ruta("agente")
    data object Detalle : Ruta("detalle/{folio}") {
        fun crear(folio: String) = "detalle/$folio"
    }
    data object Comprobante : Ruta("comprobante/{folio}") {
        fun crear(folio: String) = "comprobante/$folio"
    }
    data object HistorialPagos : Ruta("historial-pagos")
    data object ReportarProblema : Ruta("reportar-problema")
    data object HistorialReportes : Ruta("historial-reportes")
}
