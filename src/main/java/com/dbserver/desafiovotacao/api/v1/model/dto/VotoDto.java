package com.dbserver.desafiovotacao.api.v1.model.dto;

import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VotoDto {
    private Long id;
    private AssociadoDto associado;
    private LocalDateTime dataHoraVoto;
    private TipoVoto voto;
    private PautaDto pauta;
}
