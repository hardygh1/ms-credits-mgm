package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.response.CuotaResponse;
import com.cibertec.ms_credits.entity.Cuota;
import org.springframework.stereotype.Component;

@Component
public class CuotaMapper {

    public CuotaResponse toResponse(Cuota cuota) {
        return new CuotaResponse(
                cuota.getCuotaId(),
                cuota.getPrestamo().getPrestamoId(),
                cuota.getNumeroCuota(),
                cuota.getFechaVencimiento(),
                cuota.getCapital(),
                cuota.getInteres(),
                cuota.getMonto(),
                cuota.getSaldo(),
                cuota.getEstado().getParametroId(),
                cuota.getEstado().getDescripcion()
        );
    }
}
