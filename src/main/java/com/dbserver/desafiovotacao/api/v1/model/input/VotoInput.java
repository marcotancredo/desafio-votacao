package com.dbserver.desafiovotacao.api.v1.model.input;

import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VotoInput {
    @NotNull
    private Long associadoId;
    @NotNull
    private TipoVoto voto;
    @NotNull
    private Long pautaId;
}
