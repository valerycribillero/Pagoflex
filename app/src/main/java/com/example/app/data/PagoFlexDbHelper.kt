package com.example.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.app.model.Rol
import com.example.app.model.Comprobante
import com.example.app.model.Compromiso
import com.example.app.model.Servicio
import com.example.app.model.Usuario
import com.example.app.model.Reporte


class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "PagoFlex.db"
        private const val VERSION_BD = 13
        private const val TABLA_ROLES = "roles"
        private const val TABLA_USUARIOS = "usuarios"
        private const val TABLA_SERVICIOS = "servicios"
        private const val TABLA_COMPROMISOS = "compromisos"
        private const val TABLA_PAGOS = "pagos"
        private const val TABLA_REPORTES = "reportes"

        private const val ID_ROL = "id_rol"
        private const val NOMBRE_ROL = "nombre_rol"

        private const val ID_USUARIO = "id_usuario"
        private const val RUT = "rut"
        private const val NOMBRE = "nombre"
        private const val CORREO = "correo"
        private const val CONTRASENA = "contrasena"
        private const val ID_SERVICIO = "id_servicio"
        private const val NOMBRE_EMPRESA = "nombre_empresa"
        private const val RUT_PAGADOR = "rut_pagador"
        private const val ALIAS = "alias"
        private const val FOLIO = "folio"
        private const val CONCEPTO = "concepto"
        private const val MONTO = "monto"
        private const val VENCIMIENTO = "vencimiento"
        private const val ESTADO = "estado"
        private const val RECARGO = "recargo"
        private const val NUMERO_CUOTA = "numero_cuota"
        private const val ID_COMPROMISO = "id_compromiso"
        private const val NUMERO_COMPROBANTE = "numero_comprobante"
        private const val MONTO_PAGADO = "monto_pagado"
        private const val FECHA_HORA = "fecha_hora"
        private const val CANAL = "canal"
        private const val MEDIO_PAGO = "medio_pago"
        private const val MOTIVO_REPORTE = "motivo"
        private const val ESTADO_REPORTE = "estado_reporte"
        private const val FECHA_REPORTE = "fecha_reporte"

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES ("+
                    "$ID_ROL INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$NOMBRE_ROL TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS(" +
                    "$ID_USUARIO INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "$RUT TEXT NOT NULL UNIQUE,"+
                    "$NOMBRE TEXT NOT NULL,"+
                    "$CORREO TEXT NOT NULL UNIQUE,"+
                    "$CONTRASENA TEXT NOT NULL,"+
                    "$ID_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ID_ROL))"

        private const val SQL_CREAR_SERVICIOS =
            "CREATE TABLE $TABLA_SERVICIOS (" +
                    "$ID_SERVICIO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ID_USUARIO INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($ID_USUARIO), " +
                    "$NOMBRE_EMPRESA TEXT NOT NULL, " +
                    "$RUT_PAGADOR TEXT NOT NULL, " +
                    "$ALIAS TEXT NOT NULL)"

        private const val SQL_CREAR_COMPROMISOS =
            "CREATE TABLE $TABLA_COMPROMISOS (" +
                    "$ID_COMPROMISO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FOLIO TEXT NOT NULL UNIQUE, " +
                    "$ID_USUARIO INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($ID_USUARIO), " +
                    "$NOMBRE_EMPRESA TEXT NOT NULL, $CONCEPTO TEXT NOT NULL, " +
                    "$MONTO INTEGER NOT NULL CHECK($MONTO > 0), " +
                    "$VENCIMIENTO TEXT NOT NULL, $ESTADO TEXT NOT NULL, " +
                    "$RECARGO INTEGER NOT NULL DEFAULT 0, $NUMERO_CUOTA TEXT NOT NULL DEFAULT '')"

        private const val SQL_CREAR_PAGOS =
            "CREATE TABLE $TABLA_PAGOS (" +
                    "id_pago INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$NUMERO_COMPROBANTE TEXT NOT NULL UNIQUE, " +
                    "$FOLIO TEXT NOT NULL UNIQUE REFERENCES $TABLA_COMPROMISOS($FOLIO), " +
                    "$NOMBRE_EMPRESA TEXT NOT NULL, $CONCEPTO TEXT NOT NULL, " +
                    "$MONTO_PAGADO INTEGER NOT NULL, $FECHA_HORA TEXT NOT NULL, $CANAL TEXT NOT NULL, $MEDIO_PAGO TEXT NOT NULL)"

        private const val SQL_CREAR_REPORTES =
            "CREATE TABLE $TABLA_REPORTES (" +
                    "id_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FOLIO TEXT NOT NULL UNIQUE REFERENCES $TABLA_COMPROMISOS($FOLIO), " +
                    "$MOTIVO_REPORTE TEXT NOT NULL, " +
                    "$ESTADO_REPORTE TEXT NOT NULL DEFAULT 'Pendiente', " +
                    "$FECHA_REPORTE TEXT NOT NULL)"

        private const val SQL_PROTEGER_PAGOS_UPDATE =
            "CREATE TRIGGER IF NOT EXISTS pagos_sin_modificacion " +
                    "BEFORE UPDATE ON $TABLA_PAGOS BEGIN SELECT RAISE(ABORT, 'El comprobante es inmutable'); END"

        private const val SQL_PROTEGER_PAGOS_DELETE =
            "CREATE TRIGGER IF NOT EXISTS pagos_sin_eliminacion " +
                    "BEFORE DELETE ON $TABLA_PAGOS BEGIN SELECT RAISE(ABORT, 'El comprobante es inmutable'); END"

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ID_ROL, $NOMBRE_ROL) VALUES " +
                    "(1, 'CLIENTE'), (2, 'AGENTE')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO $TABLA_USUARIOS ($RUT, $NOMBRE, $CORREO, $CONTRASENA, $ID_ROL) VALUES " +
                    "('20.435.214-5', 'Sofia Perez', 'sofia@duocuc.cl', 'cli123', 1), " +
                    "('10.625.244-K', 'Matias Sanchez', 'matias@pagoflex.com', 'age123', 2)"


        private const val SQL_PRECARGAR_COMPROMISOS_CLIENTE =
            "INSERT OR IGNORE INTO $TABLA_COMPROMISOS " +
                    "($FOLIO, $ID_USUARIO, $NOMBRE_EMPRESA, $CONCEPTO, $MONTO, $VENCIMIENTO, $ESTADO, $RECARGO) VALUES " +
                    "('PF-UNI-001', 1, 'Duoc Uc', 'Mensualidad', 356000, '2026-10-25', 'Pendiente', 7000), " +
                    "('PF-LUZ-001', 1, 'Enel', 'Luz', 28000, '2026-10-20', 'Pendiente', 1100), " +
                    "('PF-COND-001', 1, 'Condominio', 'Gasto común', 45000, '2026-10-28', 'Pendiente', 3000), " +
                    "('PF-AGUA-001', 1, 'Aguas Andinas', 'Agua', 35000, '2026-10-29', 'Pendiente', 2000)"

        private const val SQL_MIGRAR_COMPROMISOS_CLIENTE =
            "INSERT OR IGNORE INTO $TABLA_COMPROMISOS " +
                    "($FOLIO, $ID_USUARIO, $NOMBRE_EMPRESA, $CONCEPTO, $MONTO, $VENCIMIENTO, $ESTADO, $RECARGO) " +
                    "SELECT 'PF-UNI-001', $ID_USUARIO, 'Duoc Uc', 'Mensualidad', 356000, '2026-10-25', 'Pendiente', 7000 " +
                    "FROM $TABLA_USUARIOS WHERE $RUT = '20.435.214-5' " +
                    "UNION ALL SELECT 'PF-LUZ-001', $ID_USUARIO, 'Enel', 'Luz', 28000, '2026-10-20', 'Pendiente', 1100 " +
                    "FROM $TABLA_USUARIOS WHERE $RUT = '20.435.214-5' " +
                    "UNION ALL SELECT 'PF-COND-001', $ID_USUARIO, 'Condominio', 'Gasto común', 45000, '2026-10-28', 'Pendiente', 3000 " +
                    "FROM $TABLA_USUARIOS WHERE $RUT = '20.435.214-5' " +
                    "UNION ALL SELECT 'PF-AGUA-001', $ID_USUARIO, 'Aguas Andinas', 'Agua', 35000, '2026-10-29', 'Pendiente', 2000 " +
                    "FROM $TABLA_USUARIOS WHERE $RUT = '20.435.214-5'"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$NOMBRE_ROL AS $NOMBRE_ROL " +
                    "FROM $TABLA_USUARIOS u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$ID_ROL = r.$ID_ROL"

    }
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_SERVICIOS)
        db.execSQL(SQL_CREAR_COMPROMISOS)
        db.execSQL(SQL_CREAR_PAGOS)
        db.execSQL(SQL_CREAR_REPORTES)
        db.execSQL(SQL_PROTEGER_PAGOS_UPDATE)
        db.execSQL(SQL_PROTEGER_PAGOS_DELETE)
        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_COMPROMISOS_CLIENTE)
        db.execSQL(
            "INSERT INTO $TABLA_SERVICIOS ($ID_USUARIO, $NOMBRE_EMPRESA, $RUT_PAGADOR, $ALIAS) " +
                    "VALUES (1, 'Enel', '20.435.214-5', 'Cuenta de electricidad')"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS $TABLA_SERVICIOS")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
            onCreate(db)
            return
        } else if (oldVersion < 5) {
            db.execSQL(
                "CREATE TABLE servicios_nueva (" +
                    "$ID_SERVICIO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ID_USUARIO INTEGER NOT NULL REFERENCES $TABLA_USUARIOS($ID_USUARIO), " +
                    "$NOMBRE_EMPRESA TEXT NOT NULL, " +
                    "$RUT_PAGADOR TEXT NOT NULL, " +
                    "$ALIAS TEXT NOT NULL)"
            )
            db.execSQL(
                "INSERT INTO servicios_nueva " +
                    "($ID_SERVICIO, $ID_USUARIO, $NOMBRE_EMPRESA, $RUT_PAGADOR, $ALIAS) " +
                    "SELECT $ID_SERVICIO, $ID_USUARIO, $NOMBRE_EMPRESA, rut_empresa, $ALIAS " +
                    "FROM $TABLA_SERVICIOS"
            )
            db.execSQL("DROP TABLE $TABLA_SERVICIOS")
            db.execSQL("ALTER TABLE servicios_nueva RENAME TO $TABLA_SERVICIOS")
        }
        if (oldVersion < 7) {
            db.execSQL(
                "INSERT INTO $TABLA_SERVICIOS ($ID_USUARIO, $NOMBRE_EMPRESA, $RUT_PAGADOR, $ALIAS) " +
                        "SELECT 1, 'Luz', '20.435.214-5', 'Enel' " +
                        "WHERE EXISTS (SELECT 1 FROM $TABLA_USUARIOS WHERE $ID_USUARIO = 1) " +
                        "AND NOT EXISTS (SELECT 1 FROM $TABLA_SERVICIOS WHERE $ID_USUARIO = 1 AND $NOMBRE_EMPRESA = 'Enel')"
            )
        }
        if (oldVersion < 9) {
            db.execSQL("UPDATE $TABLA_COMPROMISOS SET $NOMBRE_EMPRESA = 'Duoc Uc' WHERE $FOLIO = 'PF-UNI-001' OR $NOMBRE_EMPRESA = 'Universidad'")
            db.execSQL("UPDATE $TABLA_SERVICIOS SET $NOMBRE_EMPRESA = 'Enel' WHERE $NOMBRE_EMPRESA = 'Luz'")
        }
        if (oldVersion < 10) {
            db.execSQL(SQL_MIGRAR_COMPROMISOS_CLIENTE)
        }
        if (oldVersion < 11) {
            db.execSQL(SQL_CREAR_REPORTES)
        }
        if (oldVersion < 13) {
            db.execSQL("DELETE FROM $TABLA_USUARIOS WHERE $CORREO = 'miranda@empresa.com'")
            db.execSQL("DELETE FROM $TABLA_ROLES WHERE $NOMBRE_ROL = 'EJECUTIVO'")
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion >= 8 && newVersion >= 7) return
        super.onDowngrade(db, oldVersion, newVersion)
    }

    fun listarCompromisos(idUsuario: Long? = null): List<Compromiso> {
        val lista = mutableListOf<Compromiso>()
        val foliosVencidos = mutableListOf<String>()
        val filtro = if (idUsuario == null) "" else "WHERE c.$ID_USUARIO = ?"
        val argumentos = if (idUsuario == null) null else arrayOf(idUsuario.toString())
        readableDatabase.rawQuery(
            "SELECT c.*, u.$RUT, u.$NOMBRE, u.$CORREO, u.$ID_ROL " +
                    "FROM $TABLA_COMPROMISOS c INNER JOIN $TABLA_USUARIOS u ON c.$ID_USUARIO = u.$ID_USUARIO " +
                    "$filtro ORDER BY c.$VENCIMIENTO, c.$ID_COMPROMISO",
            argumentos
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val vencimiento = cursor.getString(cursor.getColumnIndexOrThrow(VENCIMIENTO))
                var estado = cursor.getString(cursor.getColumnIndexOrThrow(ESTADO))
                if (estado == "Pendiente" && vencimiento < java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())) {
                    estado = "Vencido"
                    foliosVencidos.add(cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)))
                }
                lista.add(
                    Compromiso(
                        folio = cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)),
                        empresa = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_EMPRESA)),
                        usuario = Usuario(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(ID_USUARIO)),
                            rut = cursor.getString(cursor.getColumnIndexOrThrow(RUT)),
                            nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE)),
                            correo = cursor.getString(cursor.getColumnIndexOrThrow(CORREO)),
                            rolId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_ROL)),
                            rolNombre = "CLIENTE"
                        ),
                        concepto = cursor.getString(cursor.getColumnIndexOrThrow(CONCEPTO)),
                        monto = cursor.getInt(cursor.getColumnIndexOrThrow(MONTO)),
                        vencimiento = vencimiento,
                        estado = estado,
                        recargo = cursor.getInt(cursor.getColumnIndexOrThrow(RECARGO)),
                        numeroCuota = cursor.getString(cursor.getColumnIndexOrThrow(NUMERO_CUOTA))
                    )
                )
            }
        }
        foliosVencidos.forEach { vencido ->
            writableDatabase.execSQL("UPDATE $TABLA_COMPROMISOS SET $ESTADO = 'Vencido' WHERE $FOLIO = ?", arrayOf(vencido))
        }
        return lista
    }

    fun insertarCompromiso(compromiso: Compromiso, idUsuario: Long): Long = writableDatabase.insert(
        TABLA_COMPROMISOS, null, ContentValues().apply {
            put(FOLIO, compromiso.folio)
            put(ID_USUARIO, idUsuario)
            put(NOMBRE_EMPRESA, compromiso.empresa)
            put(CONCEPTO, compromiso.concepto)
            put(MONTO, compromiso.monto)
            put(VENCIMIENTO, compromiso.vencimiento)
            put(ESTADO, compromiso.estado)
            put(RECARGO, compromiso.recargo)
            put(NUMERO_CUOTA, compromiso.numeroCuota)
        }
    )

    fun eliminarCompromiso(folio: String, idUsuario: Long): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val tienePagoOReporte = db.rawQuery(
                "SELECT 1 FROM $TABLA_PAGOS WHERE $FOLIO = ? " +
                        "UNION ALL SELECT 1 FROM $TABLA_REPORTES WHERE $FOLIO = ? LIMIT 1",
                arrayOf(folio, folio)
            ).use { it.moveToFirst() }
            if (tienePagoOReporte) return false

            val eliminados = db.delete(
                TABLA_COMPROMISOS,
                "$FOLIO = ? AND $ID_USUARIO = ? AND $ESTADO IN ('Pendiente', 'Vencido')",
                arrayOf(folio, idUsuario.toString())
            )
            if (eliminados != 1) return false

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun marcarCompromisoEnRevision(folio: String): Boolean {
        val valores = ContentValues().apply { put(ESTADO, "En revisión") }
        return writableDatabase.update(
            TABLA_COMPROMISOS,
            valores,
            "$FOLIO = ? AND $ESTADO IN ('Pendiente', 'Vencido')",
            arrayOf(folio)
        ) == 1
    }

    fun registrarReporte(folio: String, motivo: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val actualizado = db.update(
                TABLA_COMPROMISOS,
                ContentValues().apply { put(ESTADO, "En revisión") },
                "$FOLIO = ? AND $ESTADO IN ('Pendiente', 'Vencido')",
                arrayOf(folio)
            )
            if (actualizado != 1) return false

            val fecha = java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.US
            ).format(java.util.Date())
            val insertado = db.insert(
                TABLA_REPORTES,
                null,
                ContentValues().apply {
                    put(FOLIO, folio)
                    put(MOTIVO_REPORTE, motivo)
                    put(ESTADO_REPORTE, "Pendiente")
                    put(FECHA_REPORTE, fecha)
                }
            )
            if (insertado == -1L) return false

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun listarReportes(): List<Reporte> {
        val lista = mutableListOf<Reporte>()
        readableDatabase.rawQuery(
            "SELECT $FOLIO, $MOTIVO_REPORTE, $ESTADO_REPORTE FROM $TABLA_REPORTES ORDER BY $FECHA_REPORTE DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Reporte(
                        folio = cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)),
                        motivo = cursor.getString(cursor.getColumnIndexOrThrow(MOTIVO_REPORTE)),
                        estado = cursor.getString(cursor.getColumnIndexOrThrow(ESTADO_REPORTE))
                    )
                )
            }
        }
        return lista
    }

    fun actualizarEstadoReporte(folio: String, estado: String): Boolean {
        if (estado != "En proceso") return false
        return writableDatabase.update(
            TABLA_REPORTES,
            ContentValues().apply { put(ESTADO_REPORTE, estado) },
            "$FOLIO = ? AND $ESTADO_REPORTE = 'Pendiente'",
            arrayOf(folio)
        ) == 1
    }

    fun resolverReporte(folio: String, estadoCompromiso: String): Boolean {
        if (estadoCompromiso !in listOf("Pendiente", "Anulado")) return false
        val db = writableDatabase
        db.beginTransaction()
        try {
            val compromisoActualizado = db.update(
                TABLA_COMPROMISOS,
                ContentValues().apply { put(ESTADO, estadoCompromiso) },
                "$FOLIO = ? AND $ESTADO = 'En revisión'",
                arrayOf(folio)
            )
            if (compromisoActualizado != 1) return false

            val reporteActualizado = db.update(
                TABLA_REPORTES,
                ContentValues().apply { put(ESTADO_REPORTE, "Resuelto") },
                "$FOLIO = ? AND $ESTADO_REPORTE = 'En proceso'",
                arrayOf(folio)
            )
            if (reporteActualizado != 1) return false

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun confirmarPago(folio: String, medioPago: String): Comprobante? {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val datos = db.rawQuery(
                "SELECT * FROM $TABLA_COMPROMISOS WHERE $FOLIO = ?", arrayOf(folio)
            ).use { c ->
                if (!c.moveToFirst()) return null
                val fechaVencimiento = c.getString(c.getColumnIndexOrThrow(VENCIMIENTO))
                val estadoGuardado = c.getString(c.getColumnIndexOrThrow(ESTADO))
                val hoy = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                val estado = if (estadoGuardado == "Pendiente" && fechaVencimiento < hoy) "Vencido" else estadoGuardado
                if (estado != "Pendiente" && estado != "Vencido") return null
                Triple(
                    c.getString(c.getColumnIndexOrThrow(NOMBRE_EMPRESA)),
                    c.getString(c.getColumnIndexOrThrow(CONCEPTO)),
                    c.getInt(c.getColumnIndexOrThrow(MONTO)) + if (estado == "Vencido") c.getInt(c.getColumnIndexOrThrow(RECARGO)) else 0
                )
            }
            val bloqueo = db.rawQuery(
                "SELECT 1 FROM $TABLA_COMPROMISOS actual JOIN $TABLA_COMPROMISOS anterior " +
                        "ON actual.$ID_USUARIO = anterior.$ID_USUARIO AND actual.$NOMBRE_EMPRESA = anterior.$NOMBRE_EMPRESA " +
                        "AND (anterior.$VENCIMIENTO < actual.$VENCIMIENTO OR (anterior.$VENCIMIENTO = actual.$VENCIMIENTO AND anterior.$ID_COMPROMISO < actual.$ID_COMPROMISO)) " +
                        "WHERE actual.$FOLIO = ? AND anterior.$ESTADO IN ('Pendiente','Vencido') LIMIT 1", arrayOf(folio)
            ).use { it.moveToFirst() }
            if (bloqueo) return null
            val secuencia = db.rawQuery("SELECT COUNT(*) + 1 FROM $TABLA_PAGOS", null).use { c -> c.moveToFirst(); c.getInt(0) }
            val numero = "CPR-${secuencia.toString().padStart(6, '0')}"
            val fecha = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
            val recibo = Comprobante(
                numero = numero,
                folioCompromiso = folio,
                empresa = datos.first,
                concepto = datos.second,
                montoPagado = datos.third,
                fechaHora = fecha,
                canal = "App",
                medioPago = medioPago
            )
            val insertado = db.insert(TABLA_PAGOS, null, ContentValues().apply {
                put(NUMERO_COMPROBANTE, recibo.numero)
                put(FOLIO, recibo.folioCompromiso)
                put(NOMBRE_EMPRESA, recibo.empresa)
                put(CONCEPTO, recibo.concepto)
                put(MONTO_PAGADO, recibo.montoPagado)
                put(FECHA_HORA, recibo.fechaHora)
                put(CANAL, "App")
                put(MEDIO_PAGO, recibo.medioPago)
            })
            if (insertado == -1L) return null
            db.update(TABLA_COMPROMISOS, ContentValues().apply { put(ESTADO, "Pagado") }, "$FOLIO = ?", arrayOf(folio))
            db.setTransactionSuccessful()
            return recibo
        } finally {
            db.endTransaction()
        }
    }

    fun buscarComprobante(folio: String): Comprobante? = readableDatabase.rawQuery(
        "SELECT * FROM $TABLA_PAGOS WHERE $FOLIO = ?", arrayOf(folio)
    ).use { c ->
        if (!c.moveToFirst()) null else Comprobante(
            numero = c.getString(c.getColumnIndexOrThrow(NUMERO_COMPROBANTE)),
            folioCompromiso = c.getString(c.getColumnIndexOrThrow(FOLIO)),
            empresa = c.getString(c.getColumnIndexOrThrow(NOMBRE_EMPRESA)),
            concepto = c.getString(c.getColumnIndexOrThrow(CONCEPTO)),
            montoPagado = c.getInt(c.getColumnIndexOrThrow(MONTO_PAGADO)),
            fechaHora = c.getString(c.getColumnIndexOrThrow(FECHA_HORA)),
            canal = c.getString(c.getColumnIndexOrThrow(CANAL)),
            medioPago = c.getString(c.getColumnIndexOrThrow(MEDIO_PAGO))
        )
    }

    fun listarComprobantes(idUsuario: Long): List<Comprobante> {
        val lista = mutableListOf<Comprobante>()
        readableDatabase.rawQuery(
            "SELECT p.* FROM $TABLA_PAGOS p " +
                    "INNER JOIN $TABLA_COMPROMISOS c ON p.$FOLIO = c.$FOLIO " +
                    "WHERE c.$ID_USUARIO = ? ORDER BY p.$FECHA_HORA DESC",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Comprobante(
                        numero = cursor.getString(cursor.getColumnIndexOrThrow(NUMERO_COMPROBANTE)),
                        folioCompromiso = cursor.getString(cursor.getColumnIndexOrThrow(FOLIO)),
                        empresa = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_EMPRESA)),
                        concepto = cursor.getString(cursor.getColumnIndexOrThrow(CONCEPTO)),
                        montoPagado = cursor.getInt(cursor.getColumnIndexOrThrow(MONTO_PAGADO)),
                        fechaHora = cursor.getString(cursor.getColumnIndexOrThrow(FECHA_HORA)),
                        canal = cursor.getString(cursor.getColumnIndexOrThrow(CANAL)),
                        medioPago = cursor.getString(cursor.getColumnIndexOrThrow(MEDIO_PAGO))
                    )
                )
            }
        }
        return lista
    }

    fun listarRoles(): List<Rol>{
        val roles = mutableListOf<Rol>()
        readableDatabase.rawQuery(
            "SELECT $ID_ROL, $NOMBRE_ROL FROM $TABLA_ROLES ORDER BY $ID_ROL",
            null
        ).use {  cursor ->
            while (cursor.moveToNext()){
                roles.add(
                    Rol(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(ID_ROL)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_ROL))
                    )
                )
            }
        }
        return roles
    }

    fun insertarUsuario(usuario: Usuario, contrasena: String): Long {
        val valores = valoresDe(usuario).apply {
            put(CONTRASENA, contrasena)
        }
        return writableDatabase.insert(TABLA_USUARIOS, null, valores)
    }

    fun insertarServicio(servicio: Servicio): Long {
        return writableDatabase.insert(
            TABLA_SERVICIOS,
            null,
            ContentValues().apply {
                put(ID_USUARIO, servicio.usuarioId)
                put(NOMBRE_EMPRESA, servicio.nombreEmpresa)
                put(RUT_PAGADOR, servicio.rutPagador)
                put(ALIAS, servicio.alias)
            }
        )
    }

    fun eliminarServicio(idServicio: Long, idUsuario: Long): Boolean {
        val filasEliminadas = writableDatabase.delete(
            TABLA_SERVICIOS,
            "$ID_SERVICIO = ? AND $ID_USUARIO = ?",
            arrayOf(idServicio.toString(), idUsuario.toString())
        )
        return filasEliminadas > 0
    }

    fun listarServicios(idUsuario: Long): List<Servicio> {
        val servicios = mutableListOf<Servicio>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLA_SERVICIOS WHERE $ID_USUARIO = ? ORDER BY $ID_SERVICIO DESC",
            arrayOf(idUsuario.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                servicios.add(
                    Servicio(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(ID_SERVICIO)),
                        usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_USUARIO)),
                        nombreEmpresa = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_EMPRESA)),
                        rutPagador = cursor.getString(cursor.getColumnIndexOrThrow(RUT_PAGADOR)),
                        alias = cursor.getString(cursor.getColumnIndexOrThrow(ALIAS))
                    )
                )
            }
        }
        return servicios
    }

    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$CORREO = ? AND u.$CONTRASENA = ?",
            arrayOf(correo.trim().lowercase(), contrasena)
        ).firstOrNull()
    }

    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$NOMBRE", null)

    fun buscarUsuarios(texto: String): List<Usuario>{
        val patron = "%$texto%"

        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS " +
                    "WHERE u.$NOMBRE LIKE ? OR u.$CORREO LIKE ? OR u.$RUT LIKE ? " +
                    "ORDER BY u.$NOMBRE",
            arrayOf(patron, patron, patron)
        )
    }
    fun buscarPorId(id: Long): Usuario? {
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$ID_USUARIO = ?",
            arrayOf(id.toString())
        ).firstOrNull()
    }

    fun actualizarUsuario(usuario: Usuario): Int {
        return writableDatabase.update(
            TABLA_USUARIOS,
            valoresDe(usuario),
            "$ID_USUARIO = ?",
            arrayOf(usuario.id.toString())
        )
    }

    fun eliminarUsuario(id: Long): Int {
        return writableDatabase.delete(
            TABLA_USUARIOS,
            "$ID_USUARIO = ?",
            arrayOf(id.toString())
        )
    }

    private fun valoresDe(usuario: Usuario) = ContentValues().apply {
            put(RUT, usuario.rut)
            put(NOMBRE, usuario.nombre)
            put(CORREO, usuario.correo)
            put(ID_ROL, usuario.rolId)
    }

    private fun consultarUsuarios(sql: String, argumentos: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()

        readableDatabase.rawQuery(sql, argumentos).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }
    private fun leerUsuario(cursor: Cursor): Usuario {
        return Usuario(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(ID_USUARIO)),
            rut = cursor.getString(cursor.getColumnIndexOrThrow(RUT)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE)),
            correo = cursor.getString(cursor.getColumnIndexOrThrow(CORREO)),
            rolId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_ROL)),
            rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_ROL))
        )
    }
}
