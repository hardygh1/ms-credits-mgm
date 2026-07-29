package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.request.PagoRequest;
import com.cibertec.ms_credits.dto.response.PagoResponse;
import com.cibertec.ms_credits.entity.Cuota;
import com.cibertec.ms_credits.entity.Pago;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PagoMapper {

    public Pago toEntity(Cuota cuota, PagoRequest request) {
        return Pago.builder()
                .cuota(cuota)
                .fechaPago(LocalDateTime.now())
                .montoPagado(request.montoPagado())
                .metodoPago(request.metodoPago())
                .build();
    }

    public PagoResponse toResponse(Pago pago) {
        Cuota cuota = pago.getCuota();

        return new PagoResponse(
                pago.getPagoId(),
                cuota.getCuotaId(),
                cuota.getPrestamo().getPrestamoId(),
                cuota.getNumeroCuota(),
                pago.getFechaPago(),
                pago.getMontoPagado(),
                pago.getMetodoPago()
        );
    }
}
