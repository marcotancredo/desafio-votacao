package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.dto.AssociadoDto;
import com.dbserver.desafiovotacao.api.v1.model.input.AssociadoInput;
import com.dbserver.desafiovotacao.api.v1.serializable.AssociadoDeserializable;
import com.dbserver.desafiovotacao.api.v1.serializable.AssociadoSerializable;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.service.AssociadoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.dbserver.desafiovotacao.utils.CpfValidador.isValido;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/associados")
@Tag(name = "AssociadoController", description = "Controller para gerenciar associados")
public class AssociadoController {
    private static final Logger logger = LoggerFactory.getLogger(AssociadoController.class);
    public static final String CPF_INVALIDO = "CPF inválido.";
    private final AssociadoService associadoService;
    private final AssociadoSerializable associadoSerializable;
    private final AssociadoDeserializable associadoDeserializable;

    @GetMapping
    public ResponseEntity<Page<AssociadoDto>> listar(@RequestParam(required = false, defaultValue = "20") final int limit,
                                                @RequestParam(required = false, defaultValue = "0") final int offset) {
        Page<Associado> associados = associadoService.listar(limit, offset);
        Page<AssociadoDto> associadosDto = associados.map(associadoSerializable::toDto);
        return ResponseEntity.ok(associadosDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoDto> buscarPorId(@PathVariable final Long id) throws RegistroNaoEncontradoException {
        Associado associado = associadoService.buscarPorId(id);
        AssociadoDto associadoDto = associadoSerializable.toDto(associado);
        return ResponseEntity.ok(associadoDto);
    }

    @PostMapping
    public ResponseEntity<AssociadoDto> salvarAssociado(@RequestBody @Valid final AssociadoInput associadoInput)
            throws RegistroInvalidoException {
        if (!isValido(associadoInput.getCpf())) {
            logger.error(CPF_INVALIDO);
            throw new RegistroInvalidoException(CPF_INVALIDO);
        }

        Associado associado = associadoDeserializable.toEntity(associadoInput);
        associado = associadoService.salvar(associado);
        AssociadoDto associadoDto = associadoSerializable.toDto(associado);
        return ResponseEntity.ok(associadoDto);
    }
}
