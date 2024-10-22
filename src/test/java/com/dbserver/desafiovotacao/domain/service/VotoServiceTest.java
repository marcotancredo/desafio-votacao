package com.dbserver.desafiovotacao.domain.service;

import com.dbserver.desafiovotacao.api.v1.model.input.VotoInput;
import com.dbserver.desafiovotacao.domain.exception.RegistroInvalidoException;
import com.dbserver.desafiovotacao.domain.exception.RegistroNaoEncontradoException;
import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import com.dbserver.desafiovotacao.domain.repository.VotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class VotoServiceTest {
    @MockBean
    private VotoRepository votoRepository;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private AssociadoService associadoService;

    @Autowired
    private VotoService votoService;

    @Test
    @DisplayName("Deve retornar uma lista de votos com sucesso")
    public void testListarVotos() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(votoRepository.findAll(pageRequest)).thenReturn(Page.empty());

        Page<Voto> votos = votoService.listar(10, 0);

        assertNotNull(votos);
        verify(votoRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("Deve lançar RegistroNaoEncontradoException quando o voto não for encontrado")
    public void testBuscarVotoPorId_RegistroNaoEncontrado() {
        when(votoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RegistroNaoEncontradoException exception =
                assertThrows(RegistroNaoEncontradoException.class, () -> votoService.buscarPorId(1L));

        verify(votoRepository).findById(1L);
        assertEquals(exception.getMessage(), "Não foi possível localizar voto com o id: [1].");
    }

    @Test
    @DisplayName("Deve salvar um voto com sucesso")
    public void testSalvarVoto_Sucesso() throws RegistroNaoEncontradoException, RegistroInvalidoException {
        VotoInput votoInput = createVotoInput();
        Pauta pauta = createPautaAberta();
        Associado associado = createAssociado();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(associadoService.buscarPorId(1L)).thenReturn(associado);
        when(votoRepository.existsVotoByAssociadoAndPauta(associado, pauta)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(new Voto(associado, TipoVoto.SIM, pauta));

        Voto voto = votoService.salvar(votoInput);

        assertNotNull(voto);
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando a sessão não estiver aberta ao salvar um voto")
    public void testSalvarVoto_SessaoNaoAberta() throws RegistroNaoEncontradoException {
        VotoInput votoInput = createVotoInput();
        Pauta pauta = createPautaAguardandoAbertura();
        Associado associado = createAssociado();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(associadoService.buscarPorId(1L)).thenReturn(associado);

        assertThrows(RegistroInvalidoException.class, () -> votoService.salvar(votoInput));

        verify(pautaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando a sessão estiver encerrada ao salvar um voto")
    public void testSalvarVoto_SessaoEncerrada() throws RegistroNaoEncontradoException {
        VotoInput votoInput = createVotoInput();
        Pauta pauta = createPautaEncerrada();
        Associado associado = createAssociado();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(associadoService.buscarPorId(1L)).thenReturn(associado);

        assertThrows(RegistroInvalidoException.class, () -> votoService.salvar(votoInput));

        verify(pautaService).buscarPorId(1L);
    }

    private static Pauta createPautaEncerrada() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setSituacao(SituacaoPauta.VOTACAO_ENCERRADA);
        return pauta;
    }

    @Test
    @DisplayName("Deve lançar RegistroInvalidoException quando o associado já tiver votado na pauta")
    public void testSalvarVoto_AssociadoJaVotou() throws RegistroNaoEncontradoException {
        VotoInput votoInput = createVotoInput();
        Pauta pauta = createPautaAberta();
        Associado associado = createAssociado();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(associadoService.buscarPorId(1L)).thenReturn(associado);
        when(votoRepository.existsVotoByAssociadoAndPauta(associado, pauta)).thenReturn(true);

        assertThrows(RegistroInvalidoException.class, () -> votoService.salvar(votoInput));

        verify(votoRepository).existsVotoByAssociadoAndPauta(associado, pauta);
    }

    private static Associado createAssociado() {
        Associado associado = new Associado();
        associado.setId(1L);
        return associado;
    }

    private static Pauta createPautaAberta() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setSituacao(SituacaoPauta.VOTACAO_ABERTA);
        return pauta;
    }

    private static Pauta createPautaAguardandoAbertura() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setSituacao(SituacaoPauta.AGUARDANDO_ABERTURA);
        return pauta;
    }

    private static VotoInput createVotoInput() {
        VotoInput votoInput = new VotoInput();
        votoInput.setAssociadoId(1L);
        votoInput.setPautaId(1L);
        votoInput.setVoto(TipoVoto.SIM);
        return votoInput;
    }
}