package com.cibertec.ms_credits.constants;

public final class ApiMessages {

    private ApiMessages() {
    }

    public static final String VALIDATION_ERROR = "Los datos enviados no son válidos";
    public static final String UNAUTHORIZED = "Credenciales inválidas o token no proporcionado";
    public static final String FORBIDDEN = "No tiene permisos para realizar esta acción";
    public static final String INTERNAL_ERROR = "Ocurrió un error inesperado, intente nuevamente";

    // Auth
    public static final String CORREO_YA_REGISTRADO = "El correo ya se encuentra registrado";
    public static final String USUARIO_REGISTRADO = "Usuario registrado correctamente";
    public static final String LOGIN_EXITOSO = "Inicio de sesión exitoso";
    public static final String NOMBRE_REQUERIDO = "El nombre es obligatorio";
    public static final String CORREO_REQUERIDO = "El correo es obligatorio";
    public static final String CORREO_INVALIDO = "El correo no tiene un formato válido";
    public static final String PASSWORD_REQUERIDO = "La contraseña es obligatoria";
    public static final String PASSWORD_MIN_LENGTH = "La contraseña debe tener al menos 6 caracteres";
    public static final String ROL_REQUERIDO = "El rol es obligatorio";

    // Clientes
    public static final String DOCUMENTO_YA_REGISTRADO = "El documento ya se encuentra registrado";
    public static final String CLIENTE_REGISTRADO = "Cliente registrado correctamente";
    public static final String CLIENTES_LISTADOS = "Clientes obtenidos correctamente";
    public static final String CLIENTE_NO_ENCONTRADO = "Cliente no encontrado";
    public static final String NUMERO_DOCUMENTO_REQUERIDO = "El número de documento es obligatorio";
    public static final String NUMERO_DOCUMENTO_MAX_LENGTH = "El número de documento no debe exceder los 15 caracteres";
    public static final String TIPO_DOCUMENTO_REQUERIDO = "El tipo de documento es obligatorio";
    public static final String TIPO_DOCUMENTO_NO_ENCONTRADO = "El tipo de documento indicado no existe";
    public static final String NOMBRES_REQUERIDO = "Los nombres son obligatorios";
    public static final String NOMBRES_MAX_LENGTH = "Los nombres no deben exceder los 100 caracteres";
    public static final String APELLIDOS_REQUERIDO = "Los apellidos son obligatorios";
    public static final String APELLIDOS_MAX_LENGTH = "Los apellidos no deben exceder los 100 caracteres";
    public static final String CLIENTE_OBTENIDO = "Cliente obtenido correctamente";
    public static final String CLIENTE_ACTUALIZADO = "Cliente actualizado correctamente";
    public static final String CLIENTE_ESTADO_ACTUALIZADO = "Estado del cliente actualizado correctamente";
    public static final String ESTADO_ID_REQUERIDO = "El estado es obligatorio";
    public static final String ESTADO_NO_ENCONTRADO = "El estado indicado no existe";

    // Préstamos
    public static final String PRESTAMO_REGISTRADO = "Préstamo registrado correctamente";
    public static final String PRESTAMOS_LISTADOS = "Préstamos obtenidos correctamente";
    public static final String PRESTAMO_DETALLE_OBTENIDO = "Detalle del préstamo obtenido correctamente";
    public static final String PRESTAMO_APROBADO = "Préstamo aprobado correctamente";
    public static final String PRESTAMO_NO_ENCONTRADO = "Préstamo no encontrado";
    public static final String PRESTAMO_NO_PENDIENTE = "El préstamo no se encuentra en estado PENDIENTE";
    public static final String CLIENTE_ID_REQUERIDO = "El cliente es obligatorio";
    public static final String MONTO_REQUERIDO = "El monto es obligatorio";
    public static final String MONTO_INVALIDO = "El monto debe ser positivo, con máximo 10 dígitos enteros y 2 decimales";
    public static final String TASA_INTERES_REQUERIDA = "La tasa de interés es obligatoria";
    public static final String TASA_INTERES_INVALIDA = "La tasa de interés debe ser positiva, con máximo 3 dígitos enteros y 2 decimales";
    public static final String PLAZO_MESES_REQUERIDO = "El plazo en meses es obligatorio";
    public static final String PLAZO_MESES_INVALIDO = "El plazo debe estar entre 1 y 360 meses";

    // Cuotas y Pagos
    public static final String CUOTAS_LISTADAS = "Cuotas obtenidas correctamente";
    public static final String PAGO_REGISTRADO = "Pago registrado correctamente";
    public static final String PAGOS_LISTADOS = "Pagos obtenidos correctamente";
    public static final String PRESTAMO_NO_APROBADO = "El préstamo debe estar aprobado para registrar pagos";
    public static final String PRESTAMO_SIN_CUOTAS_PENDIENTES = "El préstamo no tiene cuotas pendientes de pago";
    public static final String MONTO_PAGADO_REQUERIDO = "El monto pagado es obligatorio";
    public static final String MONTO_PAGADO_INVALIDO = "El monto pagado debe ser positivo, con máximo 10 dígitos enteros y 2 decimales";
    public static final String MONTO_PAGADO_NO_COINCIDE = "El monto pagado debe ser igual al monto de la cuota pendiente";
    public static final String METODO_PAGO_REQUERIDO = "El método de pago es obligatorio";
    public static final String METODO_PAGO_MAX_LENGTH = "El método de pago no debe exceder los 30 caracteres";

    // Parametros
    public static final String PARAMETROS_LISTADOS = "Parámetros obtenidos correctamente";
    public static final String PARAMETRO_NO_CONFIGURADO = "El sistema no tiene configurado un parámetro requerido, contacte al administrador";

    // Cotizaciones
    public static final String COTIZACION_REGISTRADA = "Cotización registrada correctamente";
    public static final String COTIZACIONES_LISTADAS = "Cotizaciones obtenidas correctamente";
    public static final String COTIZACION_DETALLE_OBTENIDO = "Detalle de la cotización obtenido correctamente";
    public static final String COTIZACION_APROBADA = "Cotización aprobada correctamente, préstamo generado";
    public static final String COTIZACION_ELIMINADA = "Cotización anulada correctamente";
    public static final String COTIZACION_NO_ENCONTRADA = "Cotización no encontrada";
    public static final String COTIZACION_YA_APROBADA = "La cotización ya fue aprobada";
    public static final String COTIZACION_ELIMINADA_NO_APROBAR = "La cotización fue anulada y no puede aprobarse";
    public static final String COTIZACION_APROBADA_NO_ELIMINAR = "No se puede anular una cotización ya aprobada";
    public static final String COTIZACION_YA_ELIMINADA = "La cotización ya se encuentra anulada";
}
