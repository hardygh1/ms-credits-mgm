package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.response.DetalleCuotaResponse;
import com.cibertec.ms_credits.dto.response.PrestamoDetalleResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;
import com.cibertec.ms_credits.entity.Cliente;
import com.cibertec.ms_credits.entity.Cotizacion;
import com.cibertec.ms_credits.entity.Cuota;
import com.cibertec.ms_credits.entity.DetalleCotizacion;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.entity.Prestamo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrestamoMapper {

    public Prestamo toEntity(Cliente cliente, Cotizacion cotizacion, Parametro estado) {
        return Prestamo.builder()
                .cliente(cliente)
                .cotizacion(cotizacion)
                .estado(estado)
                .build();
    }

    public PrestamoResponse toResponse(Prestamo prestamo) {
        Cotizacion cotizacion = prestamo.getCotizacion();

        return new PrestamoResponse(
                prestamo.getPrestamoId(),
                prestamo.getCliente().getClienteId(),
                cotizacion.getCotizacionId(),
                cotizacion.getMonto(),
                cotizacion.getTasaInteres(),
                cotizacion.getPlazoMeses(),
                cotizacion.getCuotaMensual(),
                cotizacion.getTotalInteres(),
                cotizacion.getTotalPagar(),
                prestamo.getEstado().getParametroId(),
                prestamo.getEstado().getDescripcion(),
                prestamo.getFechaAprobacion(),
                prestamo.getFechaDesembolso()
        );
    }

    public PrestamoDetalleResponse toDetalleResponse(Prestamo prestamo, List<DetalleCotizacion> detalles) {
        Cotizacion cotizacion = prestamo.getCotizacion();
        List<DetalleCuotaResponse> cronograma = detalles.stream()
                .map(detalle -> new DetalleCuotaResponse(
                        detalle.getNumeroCuota(),
                        detalle.getFechaPago(),
                        detalle.getCapital(),
                        detalle.getInteres(),
                        detalle.getCuota(),
                        detalle.getSaldo()))
                .toList();

        return new PrestamoDetalleResponse(
                prestamo.getPrestamoId(),
                prestamo.getCliente().getClienteId(),
                cotizacion.getCotizacionId(),
                cotizacion.getMonto(),
                cotizacion.getTasaInteres(),
                cotizacion.getPlazoMeses(),
                cotizacion.getCuotaMensual(),
                cotizacion.getTotalInteres(),
                cotizacion.getTotalPagar(),
                prestamo.getEstado().getParametroId(),
                prestamo.getEstado().getDescripcion(),
                prestamo.getFechaAprobacion(),
                prestamo.getFechaDesembolso(),
                cronograma
        );
    }

    public List<Cuota> toCuotas(Prestamo prestamo, List<DetalleCotizacion> detalles, Parametro estadoCuota) {
        return detalles.stream()
                .map(detalle -> Cuota.builder()
                        .prestamo(prestamo)
                        .numeroCuota(detalle.getNumeroCuota())
                        .fechaVencimiento(detalle.getFechaPago())
                        .capital(detalle.getCapital())
                        .interes(detalle.getInteres())
                        .monto(detalle.getCuota())
                        .saldo(detalle.getSaldo())
                        .estado(estadoCuota)
                        .build())
                .toList();
    }
}
