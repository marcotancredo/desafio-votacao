package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.dto.AssociadoDto;
import com.dbserver.desafiovotacao.api.v1.model.dto.PautaDto;
import com.dbserver.desafiovotacao.api.v1.model.dto.VotoDto;
import com.dbserver.desafiovotacao.api.v1.model.input.VotoInput;
import com.dbserver.desafiovotacao.api.v1.serializable.VotoSerializable;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import com.dbserver.desafiovotacao.domain.service.VotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotoService votoService;

    @MockBean
    private VotoSerializable votoSerializable;

    @Test
    @DisplayName("Deve listar votos com sucesso")
    void testListarVotos() throws Exception {
        Associado associado = createAssociado();
        Pauta pauta = createPauta();
        Voto voto = createVoto(associado, pauta);

        AssociadoDto associadoDto = createAssociadoDto();
        PautaDto pautaDto = createPautaDto();
        VotoDto votoDto = createVotoDto(associadoDto, pautaDto);

        when(votoService.listar(20, 0)).thenReturn(new PageImpl<>(Collections.singletonList(voto)));
        when(votoSerializable.toDto(any(Voto.class))).thenReturn(votoDto);

        mockMvc.perform(get("/v1/votos")
                        .param("limit", "20")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}],\"pageable\":\"INSTANCE\",\"totalElements\":1,\"totalPages\":1,\"last\":true,\"size\":1,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":1,\"first\":true,\"empty\":false}"));

        verify(votoService, times(1)).listar(20, 0);
    }

    @Test
    @DisplayName("Deve buscar voto por id com sucesso")
    void testBuscarVotoPorId() throws Exception {
        Associado associado = createAssociado();
        Pauta pauta = createPauta();
        Voto voto = createVoto(associado, pauta);

        AssociadoDto associadoDto = createAssociadoDto();
        PautaDto pautaDto = createPautaDto();
        VotoDto votoDto = createVotoDto(associadoDto, pautaDto);

        when(votoService.buscarPorId(1L)).thenReturn(voto);
        when(votoSerializable.toDto(any(Voto.class))).thenReturn(votoDto);

        mockMvc.perform(get("/v1/votos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));

        verify(votoService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve salvar voto com sucesso")
    void testSalvarVoto() throws Exception {
        Associado associado = createAssociado();
        AssociadoDto associadoDto = createAssociadoDto();

        Pauta pauta = createPauta();
        PautaDto pautaDto = createPautaDto();

        Voto voto = createVoto(associado, pauta);
        VotoDto votoDto = createVotoDto(associadoDto, pautaDto);

        when(votoService.salvar(any(VotoInput.class))).thenReturn(voto);
        when(votoSerializable.toDto(any(Voto.class))).thenReturn(votoDto);

        mockMvc.perform(post("/v1/votos")
                        .contentType("application/json")
                        .content("{\"associadoId\": 1, \"pautaId\": 1, \"voto\": \"SIM\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));

        verify(votoService, times(1)).salvar(any(VotoInput.class));
    }

    private static VotoDto createVotoDto(AssociadoDto associadoDto, PautaDto pautaDto) {
        VotoDto votoDto = new VotoDto();
        votoDto.setId(1L);
        votoDto.setAssociado(associadoDto);
        votoDto.setPauta(pautaDto);
        votoDto.setVoto(TipoVoto.SIM);
        return votoDto;
    }

    private static Voto createVoto(Associado associado, Pauta pauta) {
        Voto voto = new Voto();
        voto.setId(1L);
        voto.setAssociado(associado);
        voto.setPauta(pauta);
        voto.setVoto(TipoVoto.SIM);
        return voto;
    }

    private static Pauta createPauta() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setDescricao("Pauta teste");
        pauta.setSituacao(SituacaoPauta.AGUARDANDO_ABERTURA);
        return pauta;
    }

    private static PautaDto createPautaDto() {
        PautaDto pautaDto = new PautaDto();
        pautaDto.setId(1L);
        pautaDto.setDescricao("Pauta teste");
        pautaDto.setSituacao(SituacaoPauta.AGUARDANDO_ABERTURA);
        return pautaDto;
    }

    private static Associado createAssociado() {
        Associado associado = new Associado();
        associado.setId(1L);
        associado.setNome("João da Silva");
        associado.setCpf("76119500022");
        return associado;
    }

    private static AssociadoDto createAssociadoDto() {
        AssociadoDto associadoDto = new AssociadoDto();
        associadoDto.setId(1L);
        associadoDto.setNome("João da Silva");
        associadoDto.setCpf("76119500022");
        return associadoDto;
    }
}