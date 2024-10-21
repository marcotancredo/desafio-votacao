package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.ContagemVotos;
import com.dbserver.desafiovotacao.api.v1.model.dto.PautaDto;
import com.dbserver.desafiovotacao.api.v1.model.input.PautaInput;
import com.dbserver.desafiovotacao.api.v1.serializable.PautaDeserializable;
import com.dbserver.desafiovotacao.api.v1.serializable.PautaSerializable;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.enums.Situacao;
import com.dbserver.desafiovotacao.domain.repository.PautaRepository;
import com.dbserver.desafiovotacao.domain.service.PautaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private PautaSerializable pautaSerializable;

    @MockBean
    private PautaDeserializable pautaDeserializable;

    @Autowired
    private PautaController pautaController;

    @Test
    @DisplayName("Deve salvar uma pauta com sucesso")
    void testSalvarPauta() throws Exception {
        PautaInput pautaInput = new PautaInput();
        pautaInput.setDescricao("Nova Pauta");

        Pauta pauta = new Pauta();
        when(pautaDeserializable.toEntity(any(PautaInput.class))).thenReturn(pauta);
        when(pautaService.salvar(any(Pauta.class))).thenReturn(pauta);
        when(pautaSerializable.toDto(any(Pauta.class))).thenReturn(new PautaDto());

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descricao\": \"Nova Pauta\" }"))
                .andExpect(status().isCreated());

        verify(pautaService, times(1)).salvar(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve buscar uma pauta por id com sucesso")
    void testBuscarPautaPorId() throws Exception {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setDescricao("Pauta teste");
        pauta.setSituacao(Situacao.AGUARDANDO_ABERTURA);

        when(pautaService.buscarPorId(anyLong())).thenReturn(pauta);
        when(pautaSerializable.toDto(any(Pauta.class))).thenReturn(new PautaDto());

        mockMvc.perform(get("/v1/pautas/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":null,\"descricao\":null,\"situacao\":null,\"dataHoraInicio\":null,\"dataHoraFim\":null}"));

        verify(pautaService, times(1)).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Deve listar pautas com sucesso")
    void testListarPautas() throws Exception {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setDescricao("Pauta teste");
        pauta.setSituacao(Situacao.AGUARDANDO_ABERTURA);

        PautaDto pautaDto = new PautaDto();
        pautaDto.setId(1L);
        pautaDto.setDescricao("Pauta teste");
        pautaDto.setSituacao(Situacao.AGUARDANDO_ABERTURA);

        Page<Pauta> pageMock = new PageImpl<>(Collections.singletonList(pauta));

        when(pautaService.listar(eq(null), anyInt(), anyInt())).thenReturn(pageMock);
        when(pautaSerializable.toDto(any(Pauta.class))).thenReturn(pautaDto);

        mockMvc.perform(get("/v1/pautas")
                        .param("limit", "20")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1,\"descricao\":\"Pauta teste\",\"situacao\":\"AGUARDANDO_ABERTURA\",\"dataHoraInicio\":null,\"dataHoraFim\":null}],\"pageable\":\"INSTANCE\",\"totalElements\":1,\"totalPages\":1,\"last\":true,\"size\":1,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":1,\"first\":true,\"empty\":false}"));

        verify(pautaService, times(1)).listar(eq(null), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Deve abrir votação para uma pauta com sucesso")
    void testAbrirVotacao() throws Exception {
        Pauta pauta = new Pauta();
        when(pautaService.abrirVotacao(anyLong(), any(LocalDateTime.class))).thenReturn(pauta);
        when(pautaSerializable.toDto(any(Pauta.class))).thenReturn(new PautaDto());

        mockMvc.perform(put("/v1/pautas/{id}/abrir-votacao", "1")
                        .param("dataHoraFim", "2024-10-20T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":null,\"descricao\":null,\"situacao\":null,\"dataHoraInicio\":null,\"dataHoraFim\":null}"));

        verify(pautaService, times(1)).abrirVotacao(anyLong(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar contagem de votos para uma pauta")
    void testContagemVotos() throws Exception {
        ContagemVotos contagemVotos = new ContagemVotos(1, 1);
        when(pautaService.contagemVotos(anyLong())).thenReturn(contagemVotos);

        mockMvc.perform(get("/v1/pautas/{id}/contagem-votos", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"totalVotosSim\":1,\"totalVotosNao\":1}"));

        verify(pautaService, times(1)).contagemVotos(anyLong());
    }
}