package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.repository.AssociadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AssociadoServiceTest {

    @MockBean
    private AssociadoRepository associadoRepository;

    @Autowired
    private AssociadoService associadoService;

    @Test
    @DisplayName("Deve retornar associado pelo id informado")
    void testBuscarPorId_Encontrado() throws RegistroNaoEncontradoException {
        Associado associado = new Associado();
        associado.setId(1L);
        associado.setCpf("76119500022");
        associado.setNome("João da Silva");

        when(associadoRepository.findById(anyLong())).thenReturn(Optional.of(associado));

        Associado associadoRetorno = associadoService.buscarPorId(1L);

        assertEquals(1L, associadoRetorno.getId());
        assertEquals("João da Silva", associadoRetorno.getNome());
        assertEquals("76119500022", associadoRetorno.getCpf());
    }

    @Test
    @DisplayName("Deve lançar exceção se não encontrar associado pelo id informado")
    void testBuscarPorId_NaoEncontrado() {
        when(associadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RegistroNaoEncontradoException exception =
                assertThrows(RegistroNaoEncontradoException.class, () -> associadoService.buscarPorId(1L));

        assertEquals("Não foi possível localizar associado com o id: [1].", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar associado quando tudo estiver ok")
    void testeSalvar_Ok() throws RegistroInvalidoException {
        Associado associado = new Associado();
        associado.setId(1L);
        associado.setCpf("76119500022");
        associado.setNome("João da Silva");

        when(associadoRepository.existsByCpf(ArgumentMatchers.anyString())).thenReturn(false);
        when(associadoRepository.save(any())).thenReturn(associado);

        Associado associadoSalvo = associadoService.salvar(associado);

        assertEquals(associado.getCpf(), associadoSalvo.getCpf());
        assertNotNull(associadoSalvo.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existir associado com cpf cadastrado")
    void testeSalvar_JaExisteComCpf() {
        Associado associado = new Associado();
        associado.setCpf("761.195.000-22");
        associado.setNome("João da Silva");

        when(associadoRepository.existsByCpf(ArgumentMatchers.anyString())).thenReturn(true);

        RegistroInvalidoException exception =
                assertThrows(RegistroInvalidoException.class, () -> associadoService.salvar(associado));

        assertEquals("Associado já cadastrado com o CPF: [76119500022].", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de associados existentes")
    public void testListar_AssociadosExistem() {
        Associado associado1;
        Associado associado2;

        associado1 = new Associado();
        associado1.setId(1L);
        associado1.setNome("Marco Antonio da Silva");

        associado2 = new Associado();
        associado2.setId(2L);
        associado2.setNome("João da Silva");

        List<Associado> associadosList = List.of(associado1, associado2);
        Page<Associado> pagina = mock(Page.class);
        when(associadoRepository.findAll(PageRequest.of(0, 2))).thenReturn(pagina);
        when(pagina.getContent()).thenReturn(associadosList);

        Page<Associado> resultado = associadoService.listar(2, 0);

        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals("Marco Antonio da Silva", resultado.getContent().get(0).getNome());
        assertEquals("João da Silva", resultado.getContent().get(1).getNome());
        verify(associadoRepository).findAll(PageRequest.of(0, 2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    public void testListar_NenhumAssociado() {
        List<Associado> associadosList = List.of();
        Page<Associado> paginaVazia = mock(Page.class);
        when(associadoRepository.findAll(PageRequest.of(0, 2))).thenReturn(paginaVazia);
        when(paginaVazia.getContent()).thenReturn(associadosList);

        Page<Associado> resultado = associadoService.listar(2, 0);

        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        verify(associadoRepository).findAll(PageRequest.of(0, 2));
    }
}