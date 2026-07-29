package com.cibertec.ms_credits.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula el cronograma de pagos de un préstamo bajo el sistema francés (cuota fija).
 * La tasa de interés recibida es anual y se convierte a mensual (/12/100).
 */
public final class CalculadoraPrestamoUtil {

    private static final int ESCALA = 2;
    private static final int ESCALA_INTERNA = 10;
    private static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    private CalculadoraPrestamoUtil() {
    }

    public record CuotaCalculada(int numeroCuota, LocalDate fechaPago, BigDecimal capital,
                                  BigDecimal interes, BigDecimal cuota, BigDecimal saldo) {
    }

    public record ResultadoCalculo(BigDecimal cuotaMensual, BigDecimal totalInteres,
                                    BigDecimal totalPagar, List<CuotaCalculada> cronograma) {
    }

    public static ResultadoCalculo calcular(BigDecimal monto, BigDecimal tasaInteresAnual, int plazoMeses) {
        BigDecimal tasaMensual = tasaInteresAnual.divide(BigDecimal.valueOf(1200), ESCALA_INTERNA, REDONDEO);
        BigDecimal cuotaMensual = calcularCuotaFija(monto, tasaMensual, plazoMeses).setScale(ESCALA, REDONDEO);

        List<CuotaCalculada> cronograma = new ArrayList<>();
        BigDecimal saldo = monto.setScale(ESCALA, REDONDEO);
        BigDecimal totalInteres = BigDecimal.ZERO;
        LocalDate fechaBase = LocalDate.now();

        for (int numero = 1; numero <= plazoMeses; numero++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(ESCALA, REDONDEO);
            BigDecimal capital = cuotaMensual.subtract(interes);
            BigDecimal cuotaFila = cuotaMensual;

            boolean esUltimaCuota = numero == plazoMeses;
            if (esUltimaCuota) {
                capital = saldo;
                cuotaFila = capital.add(interes).setScale(ESCALA, REDONDEO);
            }

            saldo = saldo.subtract(capital).setScale(ESCALA, REDONDEO);
            totalInteres = totalInteres.add(interes);

            cronograma.add(new CuotaCalculada(numero, fechaBase.plusMonths(numero), capital, interes, cuotaFila, saldo));
        }

        BigDecimal totalPagar = monto.setScale(ESCALA, REDONDEO).add(totalInteres);

        return new ResultadoCalculo(cuotaMensual, totalInteres.setScale(ESCALA, REDONDEO), totalPagar, cronograma);
    }

    private static BigDecimal calcularCuotaFija(BigDecimal monto, BigDecimal tasaMensual, int plazoMeses) {
        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return monto.divide(BigDecimal.valueOf(plazoMeses), ESCALA, REDONDEO);
        }

        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal factor = unoMasTasa.pow(plazoMeses);
        BigDecimal numerador = monto.multiply(tasaMensual).multiply(factor);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, ESCALA_INTERNA, REDONDEO);
    }
}
