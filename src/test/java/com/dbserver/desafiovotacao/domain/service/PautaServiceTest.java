package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.api.v1.model.ContagemVotos;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.Situacao;
import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import com.dbserver.desafiovotacao.domain.repository.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class PautaServiceTest {

    @MockBean
    private PautaRepository pautaRepository;

    @Autowired
    private PautaService pautaService;

    @Test
    @DisplayName("Salvar uma pauta com sucesso")
    public void testSalvar() {
        Pauta pauta = createPauta();

        when(pautaRepository.save(pauta)).thenReturn(pauta);

        Pauta resultado = pautaService.salvar(pauta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pautaRepository).save(pauta);
    }

    @Test
    @DisplayName("Buscar uma pauta por ID quando encontrada")
    public void testBuscarPorId_Encontrado() throws RegistroNaoEncontradoException {
        Pauta pauta = createPauta();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        Pauta resultado = pautaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pautaRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar uma pauta por ID quando não encontrada")
    public void testBuscarPorId_NaoEncontrado() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        RegistroNaoEncontradoException exception = assertThrows(RegistroNaoEncontradoException.class, () -> {
            pautaService.buscarPorId(1L);
        });

        assertEquals("Não foi possível localizar pauta com o id: [1].", exception.getMessage());
    }

    @Test
    @DisplayName("Listar pautas com filtro de situação")
    public void testListarComFiltroSituacao() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Pauta> pageMock = mock(Page.class);
        when(pautaRepository.findAll(any(Example.class), eq(pageRequest))).thenReturn(pageMock);

        Page<Pauta> resultado = pautaService.listar(Situacao.VOTACAO_ABERTA, 10, 0);

        assertNotNull(resultado);
        verify(pautaRepository).findAll(any(Example.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Listar todas as pautas sem filtro de situação")
    public void testListarSemFiltro() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Pauta> pageMock = mock(Page.class);
        when(pautaRepository.findAll(eq(pageRequest))).thenReturn(pageMock);

        Page<Pauta> resultado = pautaService.listar(null, 10, 0);

        assertNotNull(resultado);
        verify(pautaRepository).findAll(eq(pageRequest));
    }

    @Test
    @DisplayName("Abrir votação com sucesso")
    public void testAbrirVotacao_Valida() throws RegistroNaoEncontradoException, RegistroInvalidoException {
        Pauta pauta = createPauta(Situacao.AGUARDANDO_ABERTURA, null);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any())).thenReturn(pauta);

        Pauta resultado = pautaService.abrirVotacao(1L, LocalDateTime.now().plusMinutes(5));

        assertNotNull(resultado);
        assertEquals(Situacao.VOTACAO_ABERTA, resultado.getSituacao());
        verify(pautaRepository).save(pauta);
    }

    @Test
    @DisplayName("Deve lançar RegistroNaoEncontradoException quando a pauta não for encontrada")
    public void testAbrirVotacao_PautaNaoEncontrada() {
        Long pautaId = 1L;

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        RegistroNaoEncontradoException exception = assertThrows(RegistroNaoEncontradoException.class, () -> {
            pautaService.abrirVotacao(pautaId, LocalDateTime.now().plusMinutes(5));
        });

        verify(pautaRepository).findById(pautaId);
        assertEquals(exception.getMessage(), "Não foi possível localizar pauta com o id: [1].");
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando a sessão já estiver aberta")
    public void testAbrirVotacao_SessaoJaAberta() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setSituacao(Situacao.VOTACAO_ABERTA);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        RegistroInvalidoException exception = assertThrows(RegistroInvalidoException.class, () -> {
            pautaService.abrirVotacao(1L, LocalDateTime.now().plusMinutes(5));
        });

        verify(pautaRepository).findById(1L);
        assertEquals(exception.getMessage(), "Já existe uma sessão com votação aberta para a pauta de id [1].");
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando a sessão já estiver encerrada")
    public void testAbrirVotacao_SessaoJaEncerrada() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setSituacao(Situacao.VOTACAO_ENCERRADA);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        RegistroInvalidoException exception = assertThrows(RegistroInvalidoException.class, () -> {
            pautaService.abrirVotacao(1L, LocalDateTime.now().plusMinutes(5));
        });

        verify(pautaRepository).findById(1L);
        assertEquals(exception.getMessage(), "A sessão de votação para a pauta [1], já está encerrada.");
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando a data/hora de fim for inválida")
    public void testAbrirVotacao_DataHoraFimInvalida() {
        Pauta pauta = createPauta(Situacao.AGUARDANDO_ABERTURA, LocalDateTime.now().minusMinutes(10));

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        LocalDateTime dataHoraFimInvalida = LocalDateTime.now().minusMinutes(5);

        RegistroInvalidoException exception = assertThrows(RegistroInvalidoException.class, () -> {
            pautaService.abrirVotacao(1L, dataHoraFimInvalida);
        });

        verify(pautaRepository).findById(1L);
        assertEquals(exception.getMessage(), "Data/hora de finalização da sessão não pode ser anterior a data/hora de início.");
    }


    @Test
    @DisplayName("Contar votos de uma pauta")
    public void testContagemVotos() throws RegistroNaoEncontradoException {
        Pauta pauta = createPauta();

        Voto votoSim = new Voto();
        votoSim.setVoto(TipoVoto.SIM);
        Voto votoNao = new Voto();
        votoNao.setVoto(TipoVoto.NAO);

        pauta.setVotos(List.of(votoSim, votoNao));

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        ContagemVotos resultado = pautaService.contagemVotos(1L);

        assertEquals(1, resultado.getTotalVotosSim());
        assertEquals(1, resultado.getTotalVotosNao());
    }

    @Test
    @DisplayName("Encerrar votação com sucesso")
    public void testEncerrarVotacao() {
        Pauta pauta = createPauta(Situacao.VOTACAO_ABERTA, LocalDateTime.now().minusMinutes(10));

        List<Pauta> pautas = List.of(pauta);
        when(pautaRepository.findBySituacao(any())).thenReturn(pautas);

        pautaService.encerrarVotacao();
        verify(pautaRepository).save(pauta);
    }



    private static Pauta createPauta() {
        return createPauta(Situacao.VOTACAO_ENCERRADA, LocalDateTime.now());
    }

    private static Pauta createPauta(Situacao situacao, LocalDateTime dataHoraInicio) {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setDescricao("Pauta para teste");
        pauta.setSituacao(situacao);
        pauta.setDataHoraInicio(dataHoraInicio);
        if (Objects.equals(Situacao.VOTACAO_ABERTA, situacao)) {
            pauta.setDataHoraFim(dataHoraInicio.plusMinutes(1));
        }
        return pauta;
    }

}