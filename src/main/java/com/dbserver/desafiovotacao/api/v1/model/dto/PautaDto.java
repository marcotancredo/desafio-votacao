package com.dbserver.desafiovotacao.api.v1.model.dto;

import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PautaDto {

    private Long id;
    private String descricao;
    private SituacaoPauta situacao;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    @JsonIgnore
    private List<VotoDto> votos;
}
