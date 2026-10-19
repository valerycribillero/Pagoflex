
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
import com.example.app.ui.screens.EmpresaScreen
import com.example.app.ui.screens.FormularioScreen
import com.example.app.ui.screens.HistorialPagosScreen
import com.example.app.ui.screens.InicioScreen
import com.example.app.ui.screens.LoginScreen
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
        if (idUsuario == -1L) null else dbHelper.buscarPorId(idUsuario)
    }

    // Cargar siempre los datos del usuario correspondiente.
    fun cargarDatosUsuario(usuarioId: Long) {
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
                onAgregarServicio = {
                    navController.navigate(Ruta.AgregarServicio.ruta)
                },
                onVerHistorial = {
                    navController.navigate(Ruta.HistorialPagos.ruta)
                },
                onReportarProblema = {
                    navController.navigate(Ruta.ReportarProblema.ruta)
                }
            )
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
                    if (dbHelper.marcarCompromisoEnRevision(folio)) {
                        pagoFlexViewModel.reportar(folio, motivo)
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

        composable(Ruta.Agente.ruta) {
            AgenteScreen(
                reportes = reportes,
                onVolver = {
                    sesionPreferencias.borrar()
                    pagoFlexViewModel.cerrarSesion()
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.Agente.ruta) { inclusive = true }
                    }
                },
                onResolver = pagoFlexViewModel::resolverReporte
            )
        }

        composable(Ruta.Empresa.ruta) {
            EmpresaScreen(
                compromisos = compromisos,
                onVolver = {
                    sesionPreferencias.borrar()
                    pagoFlexViewModel.cerrarSesion()
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.Empresa.ruta) { inclusive = true }
                    }
                },
                onRegistrar = { rut, concepto, monto, vencimiento ->
                    val cliente = dbHelper.buscarUsuarioPorRut(rut)

                    if (cliente == null || cliente.rolNombre != "CLIENTE") {
                        false
                    } else {
                        val nuevo = Compromiso(
                            folio = "PF-${System.currentTimeMillis()}",
                            empresa = "Crédito Andino",
                            usuario = cliente,
                            concepto = concepto,
                            monto = monto,
                            vencimiento = vencimiento,
                            estado = "Pendiente",
                            recargo = 12000
                        )

                        val insertado = dbHelper.insertarCompromiso(
                            nuevo,
                            cliente.id
                        )

                        if (insertado != -1L) {
                            usuarioActual?.let {
                                pagoFlexViewModel.cargarCompromisos(
                                    dbHelper.listarCompromisos()
                                )
                            }
                        }

                        insertado != -1L
                    }
                }
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
                        if (dbHelper.marcarCompromisoEnRevision(folio)) {
                            pagoFlexViewModel.reportar(folio, motivo)
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
        "EJECUTIVO" -> Ruta.Empresa.ruta
        else -> Ruta.Login.ruta
    }
}
