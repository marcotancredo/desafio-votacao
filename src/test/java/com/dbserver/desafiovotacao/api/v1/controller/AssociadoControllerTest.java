package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.api.v1.model.dto.AssociadoDto;
import com.dbserver.desafiovotacao.api.v1.model.input.AssociadoInput;
import com.dbserver.desafiovotacao.api.v1.serializable.AssociadoDeserializable;
import com.dbserver.desafiovotacao.api.v1.serializable.AssociadoSerializable;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.service.AssociadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = AssociadoController.class)
class AssociadoControllerTest {

    @MockBean
    private AssociadoService associadoService;

    @MockBean
    private AssociadoSerializable associadoSerializable;

    @MockBean
    private AssociadoDeserializable associadoDeserializable;

    @Autowired
    private AssociadoController associadoController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve listar associados com sucesso")
    public void testListarAssociados() throws Exception {
        Associado associado = createAssociado();

        AssociadoDto associadoDto = createAssociadoDto();

        Page<Associado> associados = new PageImpl<>(Collections.singletonList(associado));

        when(associadoService.listar(20, 0)).thenReturn(associados);
        when(associadoSerializable.toDto(any())).thenReturn(associadoDto);

        String expectedJson = Files.readString(Paths.get("src/test/resources/json/test-listar-associados-result.json"));

        mockMvc.perform(get("/v1/associados")
                        .param("limit", "20")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(associadoService, times(1)).listar(20, 0);
    }

    @Test
    @DisplayName("Deve buscar associado por id com sucesso")
    public void testBuscarAssociadoPorId() throws Exception {
        Associado associado = createAssociado();
        AssociadoDto associadoDto = createAssociadoDto();

        when(associadoService.buscarPorId(anyLong())).thenReturn(associado);
        when(associadoSerializable.toDto(any())).thenReturn(associadoDto);

        String expectedJson = Files.readString(Paths.get("src/test/resources/json/test-buscar-associado-por-id-result.json"));

        mockMvc.perform(get("/v1/associados/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(associadoService, times(1)).buscarPorId(anyLong());
    }

    @Test
    @DisplayName("Deve salvar associado com sucesso")
    public void testSalvarAssociado() throws Exception {
        Associado associado = createAssociado();
        AssociadoDto associadoDto = createAssociadoDto();

        when(associadoDeserializable.toEntity(any(AssociadoInput.class))).thenReturn(associado);
        when(associadoService.salvar(any(Associado.class))).thenReturn(associado);
        when(associadoSerializable.toDto(any())).thenReturn(associadoDto);

        String expectedJson = Files.readString(Paths.get("src/test/resources/json/test-salvar-associado-result.json"));

        mockMvc.perform(post("/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"cpf\": \"00052450015\", \"nome\": \"João da Silva\" }"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(associadoService, times(1)).salvar(any(Associado.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao salvar associado com CPF inválido")
    public void testSalvarAssociadoCpfInvalido() throws Exception {
        String expectedJson = Files.readString(Paths.get("src/test/resources/json/test-salvar-associado-cpf-invalido-result.json"));

        mockMvc.perform(post("/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"cpf\": \"12345678911\", \"nome\": \"Nome Teste\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJson));

        verify(associadoService, times(0)).salvar(any(Associado.class));
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


    private static void createAssociadoInput() {
        AssociadoInput associadoInput = new AssociadoInput();
        associadoInput.setCpf("76119500022");
        associadoInput.setNome("João da Silva");
    }
}