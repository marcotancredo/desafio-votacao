package com.dbserver.desafiovotacao.domain.repository;

import com.dbserver.desafiovotacao.domain.model.Pauta;
import com.dbserver.desafiovotacao.domain.model.enums.SituacaoPauta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PautaRepositoryTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    @DisplayName("Deve encontrar pautas pela situação")
    public void testFindBySituacao() {
        Pauta pautaAberta = new Pauta();
        pautaAberta.setDescricao("Pauta aberta");
        pautaAberta.setSituacao(SituacaoPauta.VOTACAO_ABERTA);
        pautaAberta.setDataHoraInicio(LocalDateTime.now());
        pautaAberta.setDataHoraFim(LocalDateTime.now().plusHours(1));

        Pauta pautaEncerrada = new Pauta();
        pautaEncerrada.setDescricao("Pauta encerrada");
        pautaEncerrada.setSituacao(SituacaoPauta.VOTACAO_ENCERRADA);
        pautaEncerrada.setDataHoraInicio(LocalDateTime.now().minusHours(2));
        pautaEncerrada.setDataHoraFim(LocalDateTime.now().minusHours(1));

        pautaRepository.save(pautaAberta);
        pautaRepository.save(pautaEncerrada);

        List<Pauta> pautasAbertas = pautaRepository.findBySituacao(SituacaoPauta.VOTACAO_ABERTA);

        assertThat(pautasAbertas).hasSize(1);
        assertThat(pautasAbertas.get(0)).isEqualTo(pautaAberta);
    }
}