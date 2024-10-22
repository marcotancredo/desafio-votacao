package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.ContagemVotos;
import com.dbserver.desafiovotacao.api.v1.model.dto.PautaDto;
import com.dbserver.desafiovotacao.api.v1.model.input.PautaInput;
import com.dbserver.desafiovotacao.api.v1.serializable.PautaDeserializable;
import com.dbserver.desafiovotacao.api.v1.serializable.PautaSerializable;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import com.dbserver.desafiovotacao.domain.service.PautaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "PautaController", description = "Controller para gerenciar pautas")
public class PautaController {

    private final PautaService pautaService;
    private final PautaSerializable pautaSerializable;
    private final PautaDeserializable pautaDeserializable;

    @PostMapping
    public ResponseEntity<PautaDto> salvar(@RequestBody @Valid final PautaInput pautaInput) {
        Pauta pauta = pautaDeserializable.toEntity(pautaInput);
        pauta = pautaService.salvar(pauta);
        PautaDto pautaDto = pautaSerializable.toDto(pauta);
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PautaDto> buscarPorId(@PathVariable final Long id) throws RegistroNaoEncontradoException {
        Pauta pauta = pautaService.buscarPorId(id);
        PautaDto pautaDto = pautaSerializable.toDto(pauta);
        return ResponseEntity.ok(pautaDto);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PautaDto>> listar(@RequestParam(required = false) final SituacaoPauta situacao,
                                                 @RequestParam(required = false, defaultValue = "20") final int limit,
                                                 @RequestParam(required = false, defaultValue = "0") final int offset) {
        Page<Pauta> pautasList = pautaService.listar(situacao, limit, offset);
        Page<PautaDto> pautasDtoList = pautasList.map(pautaSerializable::toDto);
        return ResponseEntity.ok().body(pautasDtoList);
    }

    @GetMapping("/{id}/contagem-votos")
    public ResponseEntity<ContagemVotos> contagemVotos(@PathVariable final Long id) throws RegistroNaoEncontradoException {
        ContagemVotos contagemVotos = pautaService.contagemVotos(id);
        return ResponseEntity.ok(contagemVotos);
    }

    @PutMapping("/{id}/abrir-votacao")
    public ResponseEntity<PautaDto> abrirVotacao(@PathVariable final Long id,
                                                 @RequestParam(required = false) final LocalDateTime dataHoraFim)
            throws RegistroInvalidoException, RegistroNaoEncontradoException {
        Pauta pauta = pautaService.abrirVotacao(id, dataHoraFim);
        PautaDto pautaDto = pautaSerializable.toDto(pauta);

        return ResponseEntity.ok(pautaDto);
    }

}
