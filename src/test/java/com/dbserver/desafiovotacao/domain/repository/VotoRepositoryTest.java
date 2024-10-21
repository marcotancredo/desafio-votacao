package com.dbserver.desafiovotacao.domain.repository;

import com.dbserver.desafiovotacao.domain.model.Associado;
import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.Voto;
import com.dbserver.desafiovotacao.domain.model.enums.Situacao;
import com.dbserver.desafiovotacao.domain.model.enums.TipoVoto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VotoRepositoryTest {

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private AssociadoRepository associadoRepository;

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    @DisplayName("Deve verificar se o associado já votou na pauta")
    public void testExistsVotoByAssociadoAndPauta() {
        Associado associado = new Associado();
        associado.setNome("Associado Teste");
        associado.setCpf("00052450015");
        associado = associadoRepository.save(associado);

        Pauta pauta = new Pauta();
        pauta.setDescricao("Pauta para votação");
        pauta.setSituacao(Situacao.VOTACAO_ABERTA);
        pauta.setDataHoraInicio(LocalDateTime.now());
        pauta.setDataHoraFim(LocalDateTime.now().plusHours(1));
        pauta = pautaRepository.save(pauta);

        Voto voto = new Voto(associado, TipoVoto.SIM, pauta);
        votoRepository.save(voto);

        boolean votoExistente = votoRepository.existsVotoByAssociadoAndPauta(associado, pauta);

        assertThat(votoExistente).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso se o associado ainda não votou na pauta")
    public void testExistsVotoByAssociadoAndPautaNaoVotado() {
        Associado associado = new Associado();
        associado.setNome("Associado Teste");
        associado.setCpf("00052450015");
        associado = associadoRepository.save(associado);

        Pauta pauta = new Pauta();
        pauta.setDescricao("Pauta para votação");
        pauta.setSituacao(Situacao.VOTACAO_ABERTA);
        pauta.setDataHoraInicio(LocalDateTime.now());
        pauta.setDataHoraFim(LocalDateTime.now().plusHours(1));
        pauta = pautaRepository.save(pauta);

        boolean votoExistente = votoRepository.existsVotoByAssociadoAndPauta(associado, pauta);

        assertThat(votoExistente).isFalse();
    }
}