package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.CpfStatus;
import com.dbserver.desafiovotacao.utils.CpfValidador;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cpfs")
@Tag(name = "CpfController", description = "Controller para gerenciar validação de cpfs")
public class CpfController {
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";

    @GetMapping("/validar")
    public ResponseEntity<CpfStatus> validarCpf(@RequestParam(value = "cpf", required = false) final String cpf,
                                                @RequestParam(value = "disableRandom", required = false, defaultValue = "false") final boolean disableRandom) {
        boolean cpfValido = disableRandom ? CpfValidador.isValido(cpf) : CpfValidador.fakeValid(cpf);

        return ResponseEntity.ok(
                cpfValido ? new CpfStatus(ABLE_TO_VOTE) : new CpfStatus(UNABLE_TO_VOTE)
        );
    }
}
