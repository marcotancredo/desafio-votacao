package com.dbserver.desafiovotacao.api.v1.controller;

import com.dbserver.desafiovotacao.utils.CpfValidador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CpfController.class)
class CpfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CpfController cpfController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Deve retornar ABLE_TO_VOTE para CPF válido via API")
    public void testValidarCpfValidoViaApi() throws Exception {
        try (var cpfValidadorMock = mockStatic(CpfValidador.class)) {
            cpfValidadorMock.when(() -> CpfValidador.isValido(anyString())).thenReturn(true);

            mockMvc.perform(get("/v1/cpfs/validar")
                            .param("cpf", "12345678909")
                            .param("disableRandom", "true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"status\": \"ABLE_TO_VOTE\"}"));
        }
    }

    @Test
    @DisplayName("Deve retornar UNABLE_TO_VOTE para CPF inválido via API")
    public void testValidarCpfInvalidoViaApi() throws Exception {
        try (var cpfValidadorMock = mockStatic(CpfValidador.class)) {
            cpfValidadorMock.when(() -> CpfValidador.isValido(anyString())).thenReturn(false);

            mockMvc.perform(get("/v1/cpfs/validar")
                            .param("cpf", "12345678909")
                            .param("disableRandom", "true")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"status\": \"UNABLE_TO_VOTE\"}"));
        }
    }

    @Test
    @DisplayName("Deve retornar ABLE_TO_VOTE com fakeValid true via API")
    public void testValidarCpfComFakeValidViaApi() throws Exception {
        try (var cpfValidadorMock = mockStatic(CpfValidador.class)) {
            cpfValidadorMock.when(() -> CpfValidador.fakeValid(anyString())).thenReturn(true);

            mockMvc.perform(get("/v1/cpfs/validar")
                            .param("cpf", "12345678909")
                            .param("disableRandom", "false")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"status\": \"ABLE_TO_VOTE\"}"));
        }
    }

    @Test
    @DisplayName("Deve retornar UNABLE_TO_VOTE com fakeValid false via API")
    public void testValidarCpfComFakeInvalidViaApi() throws Exception {
        try (var cpfValidadorMock = mockStatic(CpfValidador.class)) {
            cpfValidadorMock.when(() -> CpfValidador.fakeValid(anyString())).thenReturn(false);

            mockMvc.perform(get("/v1/cpfs/validar")
                            .param("cpf", "12345678909")
                            .param("disableRandom", "false")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"status\": \"UNABLE_TO_VOTE\"}"));
        }
    }
}