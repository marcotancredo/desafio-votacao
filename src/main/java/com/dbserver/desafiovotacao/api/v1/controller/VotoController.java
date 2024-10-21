package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.dto.VotoDto;
import com.dbserver.desafiovotacao.api.v1.model.input.VotoInput;
import com.dbserver.desafiovotacao.api.v1.serializable.VotoDeserializable;
import com.dbserver.desafiovotacao.api.v1.serializable.VotoSerializable;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.service.VotoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/votos")
@RequiredArgsConstructor
@Tag(name = "VotoController", description = "Controller para gerenciar votos")
public class VotoController {

    private final VotoService votoService;
    private final VotoSerializable votoSerializable;
    private final VotoDeserializable votoDeserializable;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<VotoDto>> listar(@RequestParam(required = false, defaultValue = "20") final int limit,
                                                @RequestParam(required = false, defaultValue = "0") final int offset) {
        Page<Voto> votos = votoService.listar(limit, offset);
        Page<VotoDto> votosDto = votos.map(votoSerializable::toDto);
        return ResponseEntity.ok(votosDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VotoDto> buscarPorId(@PathVariable final Long id) throws RegistroNaoEncontradoException {
        Voto voto = votoService.buscarPorId(id);
        VotoDto votoDto = votoSerializable.toDto(voto);
        return ResponseEntity.ok(votoDto);
    }

    @PostMapping
    public ResponseEntity<VotoDto> salvar(@RequestBody @Valid final VotoInput votoInput)
            throws RegistroInvalidoException, RegistroNaoEncontradoException {
        Voto voto = votoService.salvar(votoInput);
        VotoDto votoDto = votoSerializable.toDto(voto);
        return ResponseEntity.ok(votoDto);
    }
}
