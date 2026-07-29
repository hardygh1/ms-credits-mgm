package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.request.CotizacionRequest;
import com.cibertec.ms_credits.dto.request.PrestamoRequest;
import com.cibertec.ms_credits.dto.response.CotizacionDetalleResponse;
import com.cibertec.ms_credits.dto.response.CotizacionResponse;
import com.cibertec.ms_credits.dto.response.DetalleCuotaResponse;
import com.cibertec.ms_credits.entity.Cliente;
import com.cibertec.ms_credits.entity.Cotizacion;
import com.cibertec.ms_credits.entity.DetalleCotizacion;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.util.CalculadoraPrestamoUtil.CuotaCalculada;
import com.cibertec.ms_credits.util.CalculadoraPrestamoUtil.ResultadoCalculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CotizacionMapper {

    public Cotizacion toEntity(Cliente cliente, PrestamoRequest request, ResultadoCalculo resultado, Parametro estado) {
        return construir(cliente, request.monto(), request.tasaInteres(), request.plazoMeses(), resultado, estado);
    }

    public Cotizacion toEntity(Cliente cliente, CotizacionRequest request, ResultadoCalculo resultado, Parametro estado) {
        return construir(cliente, request.monto(), request.tasaInteres(), request.plazoMeses(), resultado, estado);
    }

    public List<DetalleCotizacion> toDetalleEntities(Cotizacion cotizacion, List<CuotaCalculada> cronograma) {
        return cronograma.stream()
                .map(fila -> DetalleCotizacion.builder()
                        .cotizacion(cotizacion)
                        .numeroCuota(fila.numeroCuota())
                        .fechaPago(fila.fechaPago())
                        .capital(fila.capital())
                        .interes(fila.interes())
                        .cuota(fila.cuota())
                        .saldo(fila.saldo())
                        .build())
                .toList();
    }

    public CotizacionResponse toResponse(Cotizacion cotizacion) {
        return new CotizacionResponse(
                cotizacion.getCotizacionId(),
                cotizacion.getCliente().getClienteId(),
                cotizacion.getMonto(),
                cotizacion.getTasaInteres(),
                cotizacion.getPlazoMeses(),
                cotizacion.getCuotaMensual(),
                cotizacion.getTotalInteres(),
                cotizacion.getTotalPagar(),
                cotizacion.getEstado().getParametroId(),
                cotizacion.getEstado().getDescripcion(),
                cotizacion.getFechaRegistro()
        );
    }

    public CotizacionDetalleResponse toDetalleResponse(Cotizacion cotizacion, List<DetalleCotizacion> detalles) {
        List<DetalleCuotaResponse> cronograma = detalles.stream()
                .map(detalle -> new DetalleCuotaResponse(
                        detalle.getNumeroCuota(),
                        detalle.getFechaPago(),
                        detalle.getCapital(),
                        detalle.getInteres(),
                        detalle.getCuota(),
                        detalle.getSaldo()))
                .toList();

        return new CotizacionDetalleResponse(
                cotizacion.getCotizacionId(),
                cotizacion.getCliente().getClienteId(),
                cotizacion.getMonto(),
                cotizacion.getTasaInteres(),
                cotizacion.getPlazoMeses(),
                cotizacion.getCuotaMensual(),
                cotizacion.getTotalInteres(),
                cotizacion.getTotalPagar(),
                cotizacion.getEstado().getParametroId(),
                cotizacion.getEstado().getDescripcion(),
                cotizacion.getFechaRegistro(),
                cronograma
        );
    }

    private Cotizacion construir(Cliente cliente, BigDecimal monto, BigDecimal tasaInteres,
                                  Integer plazoMeses, ResultadoCalculo resultado, Parametro estado) {
        return Cotizacion.builder()
                .cliente(cliente)
                .monto(monto)
                .tasaInteres(tasaInteres)
                .plazoMeses(plazoMeses)
                .cuotaMensual(resultado.cuotaMensual())
                .totalInteres(resultado.totalInteres())
                .totalPagar(resultado.totalPagar())
                .estado(estado)
                .build();
    }
}
