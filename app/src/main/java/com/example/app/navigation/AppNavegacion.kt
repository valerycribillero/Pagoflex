
package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.data.PagoFlexDbHelper
import com.example.app.data.SesionPreferencias
import com.example.app.model.Compromiso
import com.example.app.ui.screens.AgenteScreen
import com.example.app.ui.screens.AgregarServicioScreen
import com.example.app.ui.screens.ComprobanteScreen
import com.example.app.ui.screens.DetalleScreen
import com.example.app.ui.screens.FormularioScreen
import com.example.app.ui.screens.HistorialPagosScreen
import com.example.app.ui.screens.HistorialReportesScreen
import com.example.app.ui.screens.InicioScreen
import com.example.app.ui.screens.LoginScreen
import com.example.app.ui.screens.MisServiciosScreen
import com.example.app.ui.screens.PerfilScreen
import com.example.app.ui.screens.ReportarProblemaScreen
import com.example.app.viewmodel.PagoFlexViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dbHelper = remember(context) {
        PagoFlexDbHelper(context.applicationContext)
    }

    val sesionPreferencias = remember(context) {
        SesionPreferencias(context.applicationContext)
    }

    val pagoFlexViewModel: PagoFlexViewModel = viewModel()

    val compromisos by pagoFlexViewModel.compromisos.collectAsStateWithLifecycle()
    val reportes by pagoFlexViewModel.reportes.collectAsStateWithLifecycle()
    val usuarioActual by pagoFlexViewModel.usuarioActual.collectAsStateWithLifecycle()
    val servicios by pagoFlexViewModel.servicios.collectAsStateWithLifecycle()

    val usuarioGuardado = remember {
        val idUsuario = sesionPreferencias.leer()
        if (idUsuario == -1L) {
            null
        } else {
            dbHelper.buscarPorId(idUsuario).also { usuario ->
                if (usuario == null) sesionPreferencias.borrar()
            }
        }
    }

    // Cargar siempre los datos del usuario correspondiente.
    fun cargarDatosUsuario(usuarioId: Long) {
        pagoFlexViewModel.cargarReportes(dbHelper.listarReportes())
        pagoFlexViewModel.cargarServicios(
            dbHelper.listarServicios(usuarioId)
        )
        pagoFlexViewModel.cargarCompromisos(
            dbHelper.listarCompromisos(usuarioId)
        )
    }

    LaunchedEffect(usuarioGuardado) {
        usuarioGuardado?.let { usuario ->
            pagoFlexViewModel.iniciarSesion(usuario)
            cargarDatosUsuario(usuario.id)

            navController.navigate(rutaParaRol(usuario.rolNombre)) {
                popUpTo(Ruta.Login.ruta) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Ruta.Login.ruta
    ) {
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIniciarSesion = dbHelper::iniciarSesion,
                onLoginExitoso = { usuario ->
                    sesionPreferencias.guardar(usuario.id)
                    pagoFlexViewModel.iniciarSesion(usuario)
                    cargarDatosUsuario(usuario.id)

                    navController.navigate(rutaParaRol(usuario.rolNombre)) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCrearCuenta = {
                    navController.navigate(Ruta.CrearCuenta.ruta)
                }
            )
        }

        composable(Ruta.CrearCuenta.ruta) {
            FormularioScreen(
                onRegistroExitoso = { usuario ->
                    sesionPreferencias.guardar(usuario.id)
                    pagoFlexViewModel.iniciarSesion(usuario)
                    cargarDatosUsuario(usuario.id)

                    navController.navigate(Ruta.Usuario.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                onVolverInicio = {
                    navController.navigateUp()
                }
            )
        }

        composable(Ruta.Usuario.ruta) {
            InicioScreen(
                usuario = usuarioActual,
                compromisos = compromisos.filter {
                    it.usuario.id == usuarioActual?.id
                },
                servicios = servicios.filter {
                    it.usuarioId == usuarioActual?.id
                },
                onVolver = {
                    sesionPreferencias.borrar()
                    pagoFlexViewModel.cerrarSesion()
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.Usuario.ruta) { inclusive = true }
                    }
                },
                onVerDetalle = { folio ->
                    navController.navigate(Ruta.Detalle.crear(folio))
                },
                onVerPerfil = {
                    navController.navigate(Ruta.Perfil.ruta)
                },
                onAgregarServicio = {
                    navController.navigate(Ruta.AgregarServicio.ruta)
                },
                onEliminarServicios = {
                    navController.navigate(Ruta.MisServicios.ruta)
                },
                onVerHistorial = {
                    navController.navigate(Ruta.HistorialPagos.ruta)
                },
                onReportarProblema = {
                    navController.navigate(Ruta.ReportarProblema.ruta)
                },
                onVerHistorialReportes = {
                    navController.navigate(Ruta.HistorialReportes.ruta)
                }
            )
        }

        composable(Ruta.Perfil.ruta) {
            val usuario = usuarioActual

            if (usuario != null) {
                PerfilScreen(
                    usuario = usuario,
                    compromisos = compromisos.filter {
                        it.usuario.id == usuario.id
                    },
                    cantidadPagos = dbHelper.listarComprobantes(usuario.id).size,
                    onVolver = {
                        navController.navigateUp()
                    },
                    reportes = reportes
                )
            }
        }

        composable(Ruta.ReportarProblema.ruta) {
            ReportarProblemaScreen(
                compromisos = compromisos.filter {
                    it.usuario.id == usuarioActual?.id
                },
                onVolver = {
                    navController.navigateUp()
                },
                onReportar = { folio, motivo ->
                    if (dbHelper.registrarReporte(folio, motivo)) {
                        pagoFlexViewModel.cargarReportes(dbHelper.listarReportes())
                        usuarioActual?.let {
                            pagoFlexViewModel.cargarCompromisos(
                                dbHelper.listarCompromisos(it.id)
                            )
                        }
                        true
                    } else {
                        false
                    }
                }
            )
        }

        composable(Ruta.HistorialPagos.ruta) {
            HistorialPagosScreen(
                comprobantes = usuarioActual
                    ?.let { dbHelper.listarComprobantes(it.id) }
                    .orEmpty(),
                onVolver = {
                    navController.navigateUp()
                },
                onVerComprobante = { folio ->
                    navController.navigate(Ruta.Comprobante.crear(folio))
                }
            )
        }

        composable(Ruta.AgregarServicio.ruta) {
            val usuario = usuarioActual

            if (usuario != null) {
                AgregarServicioScreen(
                    usuarioId = usuario.id,
                    onVolver = {
                        navController.navigateUp()
                    },
                    onServicioGuardado = {
                        navController.navigate(Ruta.MisServicios.ruta) {
                            popUpTo(Ruta.Usuario.ruta)
                            launchSingleTop = true
                        }
                    },
                    onGuardarServicio = { servicio ->
                        val idServicio = dbHelper.insertarServicio(servicio)

                        if (idServicio == -1L) {
                            false
                        } else {
                            pagoFlexViewModel.cargarServicios(
                                dbHelper.listarServicios(usuario.id)
                            )
                            true
                        }
                    }
                )
            }
        }

        composable(Ruta.MisServicios.ruta) {
            MisServiciosScreen(
                compromisos = compromisos.filter {
                    it.usuario.id == usuarioActual?.id
                },
                onVolver = {
                    navController.navigateUp()
                },
                onEliminar = { compromiso ->
                    val usuario = usuarioActual
                    if (usuario == null) {
                        false
                    } else {
                        val eliminado = dbHelper.eliminarCompromiso(compromiso.folio, usuario.id)
                        if (eliminado) {
                            pagoFlexViewModel.cargarCompromisos(
                                dbHelper.listarCompromisos(usuario.id)
                            )
                        }
                        eliminado
                    }
                }
            )
        }

        composable(Ruta.Agente.ruta) {
            AgenteScreen(
                usuario = usuarioActual,
                reportes = reportes,
                onVerPerfil = {
                    navController.navigate(Ruta.Perfil.ruta)
                },
                onVerHistorial = {
                    navController.navigate(Ruta.HistorialReportes.ruta)
                },
                onCerrarSesion = {
                    sesionPreferencias.borrar()
                    pagoFlexViewModel.cerrarSesion()
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.Agente.ruta) { inclusive = true }
                    }
                },
                onCambiarEstado = { reporte, estado ->
                    val actualizado = if (estado == "En proceso") {
                        dbHelper.actualizarEstadoReporte(reporte.folio, estado)
                    } else {
                        dbHelper.resolverReporte(reporte.folio, estado)
                    }
                    if (actualizado) {
                        pagoFlexViewModel.cargarReportes(dbHelper.listarReportes())
                        pagoFlexViewModel.cargarCompromisos(dbHelper.listarCompromisos())
                    }
                    actualizado
                }
            )
        }

        composable(Ruta.HistorialReportes.ruta) {
            val esAgente = usuarioActual?.rolNombre == "AGENTE"
            val foliosUsuario = compromisos
                .filter { it.usuario.id == usuarioActual?.id }
                .map { it.folio }
                .toSet()
            HistorialReportesScreen(
                reportes = if (esAgente) reportes else reportes.filter { it.folio in foliosUsuario },
                compromisos = compromisos.filter { it.usuario.id == usuarioActual?.id },
                esAgente = esAgente,
                onVolver = { navController.navigateUp() }
            )
        }

        composable(
            route = Ruta.Detalle.ruta,
            arguments = listOf(
                navArgument("folio") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""
            val compromiso = compromisos.find { it.folio == folio }

            if (compromiso != null) {
                DetalleScreen(
                    compromiso = compromiso,
                    onVolver = {
                        navController.navigateUp()
                    },
                    onPagar = { medioPago ->
                        val comprobante = dbHelper.confirmarPago(
                            folio,
                            medioPago
                        )

                        if (comprobante != null) {
                            usuarioActual?.let {
                                pagoFlexViewModel.cargarCompromisos(
                                    dbHelper.listarCompromisos(it.id)
                                )
                            }

                            navController.navigate(
                                Ruta.Comprobante.crear(folio)
                            ) {
                                popUpTo(Ruta.Usuario.ruta) {
                                    inclusive = false
                                }
                            }
                            true
                        } else {
                            false
                        }
                    },
                    onReportar = { motivo ->
                        if (dbHelper.registrarReporte(folio, motivo)) {
                            pagoFlexViewModel.cargarReportes(dbHelper.listarReportes())
                            usuarioActual?.let {
                                pagoFlexViewModel.cargarCompromisos(
                                    dbHelper.listarCompromisos(it.id)
                                )
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = Ruta.Comprobante.ruta,
            arguments = listOf(
                navArgument("folio") {
                    type = NavType.StringType
                }
            )
        ) { entrada ->
            val folio = entrada.arguments?.getString("folio") ?: ""

            ComprobanteScreen(
                comprobante = dbHelper.buscarComprobante(folio),
                onVolver = {
                    navController.navigateUp()
                }
            )
        }
    }
}

private fun rutaParaRol(rol: String): String {
    return when (rol) {
        "CLIENTE" -> Ruta.Usuario.ruta
        "AGENTE" -> Ruta.Agente.ruta
        else -> Ruta.Login.ruta
    }
}
