package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.response.ParametroResponse;
import com.cibertec.ms_credits.entity.Parametro;

import java.util.List;

public interface ParametroService {

    Parametro obtenerPorTipoYCodigo(String tipo, String codigo);

    Parametro obtenerPorIdYTipo(Integer parametroId, String tipoEsperado, String mensajeError);

    List<ParametroResponse> listar(String tipo);
}
