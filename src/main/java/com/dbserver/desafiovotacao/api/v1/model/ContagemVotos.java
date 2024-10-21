package com.dbserver.desafiovotacao.api.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContagemVotos {
    private long totalVotosSim;
    private long totalVotosNao;
}
